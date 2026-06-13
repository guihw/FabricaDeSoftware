import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { FeedColega } from './feed-colega';
import {
  RecomendacaoService,
  RecomendacaoCardAnfitriaoDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';


function makeCardDTO(id = 1): RecomendacaoCardAnfitriaoDTO {
  return {
    card: {
      anfitriaoId: id, nome: `Anf ${id}`, email: `anf${id}@email.com`,
      descricao: 'Casa boa', localizacao: 'SP', quartos: 2,
      classificacao: 4.5, precoMensal: 1500, arquivos: [],
    },
    score: 80, resumoCompatibilidade: 'Ótima compatibilidade',
  };
}

function makeFeedMoradia(itens: RecomendacaoCardAnfitriaoDTO[]): FeedPageDTO<RecomendacaoCardAnfitriaoDTO> {
  return { itens, pagina: 0, tamanhoPagina: 10, totalItens: itens.length, temProxima: false };
}

describe('FeedColega', () => {
  let component: FeedColega;
  let fixture: ComponentFixture<FeedColega>;
  let recomendacaoSpy: jasmine.SpyObj<RecomendacaoService>;

  const card = makeCardDTO();

  beforeEach(async () => {
    recomendacaoSpy = jasmine.createSpyObj('RecomendacaoService', [
      'feedColega', 'feedAnfitriao',
    ]);
    recomendacaoSpy.feedColega.and.returnValue(of(makeFeedMoradia([card])));

    sessionStorage.setItem('coliv_user_id', '3');

    await TestBed.configureTestingModule({
      imports: [FeedColega, CommonModule],
      providers: [
        provideRouter([]),
        { provide: RecomendacaoService, useValue: recomendacaoSpy },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(FeedColega);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve chamar feedColega com o id do sessionStorage', () => {
    expect(recomendacaoSpy.feedColega).toHaveBeenCalledWith(3, 0);
  });

  it('deve popular recomendações', () => {
    expect(component.recomendacoes().length).toBe(1);
  });

  it('deve definir carregando como false após sucesso', () => {
    expect(component.carregando()).toBeFalse();
  });

  it('deve exibir erro quando feedColega falha', fakeAsync(() => {
    recomendacaoSpy.feedColega.and.returnValue(
      throwError(() => ({ message: 'Sem conexão.' }))
    );
    component.carregarPagina(0);
    tick();
    expect(component.erro()).toBe('Sem conexão.');
  }));

  it('deve exibir erro de sessão quando não há colega_id', fakeAsync(() => {
    sessionStorage.clear();
    fixture = TestBed.createComponent(FeedColega);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tick();
    expect(component.erro()).toContain('Sessão');
  }));

  it('corScore deve retornar classe verde para score >= 80', () => {
    expect(component.corScore(80)).toContain('secondary');
  });

  it('corScore deve retornar classe primária para score entre 60 e 79', () => {
    expect(component.corScore(65)).toContain('primary');
  });

  it('corScore deve retornar classe neutra para score < 60', () => {
    expect(component.corScore(50)).toContain('surface');
  });
});