import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { signal } from '@angular/core';
import { FeedAnfitriao } from './feed-anfitriao';
import {
  RecomendacaoService,
  RecomendacaoColegaDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';
import { FotoPerfilService } from '../core/services/foto-perfil.service';

const fotoPerfilServiceStub = {
  fotoPerfilUrl: signal<string | null>(null),
  hidratar: () => {},
  hidratarComId: () => {},
  cachear: () => {},
  limpar: () => {},
};


function makeFeedColega(itens: RecomendacaoColegaDTO[]): FeedPageDTO<RecomendacaoColegaDTO> {
  return { itens, pagina: 0, tamanhoPagina: 10, totalItens: itens.length, temProxima: false };
}

describe('FeedAnfitriao', () => {
  let component: FeedAnfitriao;
  let fixture: ComponentFixture<FeedAnfitriao>;
  let recomendacaoSpy: {
    feedAnfitriao: ReturnType<typeof vi.fn>;
    feedColega: ReturnType<typeof vi.fn>;
  };

  const recColega: RecomendacaoColegaDTO = {
    colegaId: 3, nome: 'Lucas', email: 'lucas@email.com',
    score: 85, resumoCompatibilidade: 'Excelente perfil',
  };

  beforeEach(async () => {
    recomendacaoSpy = {
      feedAnfitriao: vi.fn(),
      feedColega: vi.fn(),
    };
    recomendacaoSpy.feedAnfitriao.mockReturnValue(of(makeFeedColega([recColega])));

    sessionStorage.setItem('coliv_user_id', '5');

    await TestBed.configureTestingModule({
      imports: [FeedAnfitriao, CommonModule],
      providers: [
        provideRouter([]),
        { provide: RecomendacaoService, useValue: recomendacaoSpy },
        { provide: FotoPerfilService, useValue: fotoPerfilServiceStub },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(FeedAnfitriao);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve chamar feedAnfitriao com o id do sessionStorage', () => {
    expect(recomendacaoSpy.feedAnfitriao).toHaveBeenCalledWith(5, 0);
  });

  it('deve popular recomendações após carregamento', () => {
    expect(component.recomendacoes().length).toBe(1);
    expect(component.recomendacoes()[0].nome).toBe('Lucas');
  });

  it('deve definir carregando como false após sucesso', () => {
    expect(component.carregando()).toBe(false);
  });

  it('deve exibir erro e desativar carregando quando feedAnfitriao falha', () => {
    recomendacaoSpy.feedAnfitriao.mockReturnValue(
      throwError(() => ({ message: 'Erro de rede.' }))
    );
    component.carregarPagina(0);
    expect(component.erro()).toBe('Erro de rede.');
    expect(component.carregando()).toBe(false);
  });

  it('deve exibir erro de sessão quando não há user_id no sessionStorage', () => {
    sessionStorage.clear();
    // recria componente sem user_id
    fixture = TestBed.createComponent(FeedAnfitriao);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.erro()).toContain('Sessão');
    expect(component.carregando()).toBe(false);
  });

  it('deve avançar para próxima página', () => {
    const pag1 = { ...makeFeedColega([recColega]), pagina: 0, temProxima: true };
    const pag2 = { ...makeFeedColega([recColega]), pagina: 1, temProxima: false };
    recomendacaoSpy.feedAnfitriao
      .mockReturnValueOnce(of(pag1))
      .mockReturnValueOnce(of(pag2));
    component.carregarPagina(0);
    component.temProxima.set(true);
    component.proximaPagina();
    expect(component.pagina()).toBe(1);
  });

  it('não deve avançar página quando temProxima é false', () => {
    component.carregarPagina(0);
    const chamadas = recomendacaoSpy.feedAnfitriao.mock.calls.length;
    component.proximaPagina();
    expect(recomendacaoSpy.feedAnfitriao.mock.calls.length).toBe(chamadas);
  });

  it('deve voltar para página anterior', () => {
    component.pagina.set(1);
    recomendacaoSpy.feedAnfitriao.mockReturnValue(of(makeFeedColega([recColega])));
    component.paginaAnterior();
    expect(recomendacaoSpy.feedAnfitriao).toHaveBeenCalledWith(5, 0);
  });

  it('não deve voltar antes da página 0', () => {
    component.pagina.set(0);
    const chamadas = recomendacaoSpy.feedAnfitriao.mock.calls.length;
    component.paginaAnterior();
    expect(recomendacaoSpy.feedAnfitriao.mock.calls.length).toBe(chamadas);
  });

  it('onRecusar deve remover colega localmente da lista', () => {
    component.recomendacoes.set([recColega]);
    component.onRecusar(recColega);
    expect(component.recomendacoes().length).toBe(0);
  });

  it('onRecusar não deve remover outros colegas', () => {
    const outro: RecomendacaoColegaDTO = { ...recColega, colegaId: 99, nome: 'Outro' };
    component.recomendacoes.set([recColega, outro]);
    component.onRecusar(recColega);
    expect(component.recomendacoes().length).toBe(1);
    expect(component.recomendacoes()[0].colegaId).toBe(99);
  });
});