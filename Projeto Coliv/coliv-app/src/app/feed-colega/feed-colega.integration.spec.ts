/**
 * Testes de integração: FeedColega + RecomendacaoService + MatchService
 *
 * Diferencial em relação aos testes unitários existentes (feed-colega.spec.ts):
 * - Usa RecomendacaoService e MatchService reais (sem mocks de serviço)
 * - Intercepta apenas o transporte HTTP com HttpTestingController
 * - Verifica o mapeamento de erros do ApiService (status HTTP → mensagem amigável)
 * - Testa o fluxo de like: POST /matches/novo → sessionStorage → navegação
 *
 * Nota: HttpTestingController.flush() despacha respostas de forma síncrona,
 * dispensando fakeAsync/tick().
 */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router, provideRouter } from '@angular/router';
import { vi, describe, it, expect, beforeEach, afterEach } from 'vitest';

import { FeedColega } from './feed-colega';
import {
  RecomendacaoService,
  RecomendacaoCardAnfitriaoDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';
import { MatchService, MatchResponse } from '../core/services/match.service';

const BASE = 'http://localhost:8080';

function makeCard(id = 1): RecomendacaoCardAnfitriaoDTO {
  return {
    card: {
      anfitriaoId: id,
      nome: `Anfitrião ${id}`,
      email: `anf${id}@email.com`,
      descricao: 'Casa boa',
      localizacao: 'SP',
      quartos: 2,
      classificacao: 4.5,
      precoMensal: 1500,
      arquivos: [],
      tipoVaga: null,
      comodidades: [],
    },
    score: 85,
    resumoCompatibilidade: 'Alta compatibilidade',
  };
}

function makeFeed(
  itens: RecomendacaoCardAnfitriaoDTO[],
  pagina = 0,
  temProxima = false,
): FeedPageDTO<RecomendacaoCardAnfitriaoDTO> {
  return { itens, pagina, tamanhoPagina: 10, totalItens: itens.length, temProxima };
}

describe('FeedColega (integração)', () => {
  let component: FeedColega;
  let fixture: ComponentFixture<FeedColega>;
  let httpMock: HttpTestingController;
  let router: Router;

  const FEED_URL_P0 = `${BASE}/recomendacoes/feed/colega/3?pagina=0&tamanho=10`;
  const FEED_URL_P1 = `${BASE}/recomendacoes/feed/colega/3?pagina=1&tamanho=10`;

  beforeEach(async () => {
    sessionStorage.setItem('coliv_user_id', '3');
    sessionStorage.setItem('coliv_user_tipo', 'colega');

    await TestBed.configureTestingModule({
      imports: [FeedColega],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        RecomendacaoService,
        MatchService,
      ],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    router   = TestBed.inject(Router);
    fixture  = TestBed.createComponent(FeedColega);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('deve carregar recomendações via HTTP e popular o estado do componente', () => {
    const cards = [makeCard(1), makeCard(2)];

    httpMock.expectOne(FEED_URL_P0).flush(makeFeed(cards));

    expect(component.recomendacoes().length).toBe(2);
    expect(component.recomendacoes()[0].card.anfitriaoId).toBe(1);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBeNull();
  });

  it('deve atualizar pagina e temProxima conforme resposta da API', () => {
    httpMock.expectOne(FEED_URL_P0).flush({
      itens: [makeCard()],
      pagina: 0,
      tamanhoPagina: 10,
      totalItens: 15,
      temProxima: true,
    });

    expect(component.pagina()).toBe(0);
    expect(component.temProxima()).toBe(true);
  });

  it('deve buscar página 1 via HTTP ao chamar proximaPagina() quando temProxima=true', () => {
    httpMock.expectOne(FEED_URL_P0).flush(makeFeed([makeCard(1)], 0, true));

    component.proximaPagina();

    httpMock.expectOne(FEED_URL_P1).flush(makeFeed([makeCard(2)], 1, false));

    expect(component.pagina()).toBe(1);
    expect(component.recomendacoes()[0].card.anfitriaoId).toBe(2);
    expect(component.temProxima()).toBe(false);
  });

  it('não deve fazer requisição HTTP ao chamar proximaPagina() quando temProxima=false', () => {
    httpMock.expectOne(FEED_URL_P0).flush(makeFeed([makeCard()], 0, false));

    component.proximaPagina();

    httpMock.expectNone(FEED_URL_P1);
  });

  it('não deve fazer requisição HTTP ao chamar paginaAnterior() na primeira página', () => {
    httpMock.expectOne(FEED_URL_P0).flush(makeFeed([makeCard()], 0, false));

    component.paginaAnterior();

    httpMock.expectNone(FEED_URL_P0);
  });

  it('deve mapear erro HTTP 500 para mensagem amigável via ApiService', () => {
    httpMock
      .expectOne(FEED_URL_P0)
      .flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(component.erro()).toBe('Erro interno no servidor. Tente novamente mais tarde.');
    expect(component.carregando()).toBe(false);
    expect(component.recomendacoes().length).toBe(0);
  });

  it('deve mapear erro HTTP 0 (sem conexão) para mensagem amigável', () => {
    httpMock
      .expectOne(FEED_URL_P0)
      .flush({}, { status: 0, statusText: 'Unknown Error' });

    expect(component.erro()).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
  });

  it('deve criar match via POST e marcar o card como curtido, sem navegar', () => {
    const card = makeCard(10);
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    httpMock.expectOne(FEED_URL_P0).flush(makeFeed([card]));

    component.onLike(card);

    const matchReq = httpMock.expectOne(`${BASE}/matches/novo`);
    expect(matchReq.request.method).toBe('POST');
    expect(matchReq.request.body).toEqual({
      iniciador: 'COLEGA',
      colegaId: 3,
      anfitriaoId: 10,
    });

    const matchResponse: MatchResponse = { id: 99, colegaId: 3, anfitriaoId: 10, status: 'PENDENTE' };
    matchReq.flush(matchResponse);

    expect(component.curtidos().has(10)).toBe(true);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('deve exibir erro e remover loading de like quando POST /matches/novo falha', () => {
    const card = makeCard(10);

    httpMock.expectOne(FEED_URL_P0).flush(makeFeed([card]));

    component.onLike(card);
    httpMock
      .expectOne(`${BASE}/matches/novo`)
      .flush({}, { status: 400, statusText: 'Bad Request' });

    expect(component.erro()).toBe('Dados inválidos. Verifique os campos e tente novamente.');
    expect(component.likeEmAndamento().has(10)).toBe(false);
  });

  it('deve ignorar segundo like enquanto o primeiro ainda está em andamento', () => {
    const card = makeCard(10);

    httpMock.expectOne(FEED_URL_P0).flush(makeFeed([card]));

    component.onLike(card);
    component.onLike(card); // segundo like ignorado

    const matchReqs = httpMock.match(`${BASE}/matches/novo`);
    expect(matchReqs.length).toBe(1);
    matchReqs[0].flush({ id: 99, colegaId: 3, anfitriaoId: 10, status: 'PENDENTE' });
  });
});
