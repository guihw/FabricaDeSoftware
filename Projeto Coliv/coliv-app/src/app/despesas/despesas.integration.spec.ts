/**
 * Testes de integração: Despesas + DespesaService + DivisaoService + ConviteService
 *
 * Diferencial em relação ao teste unitário existente (despesas.spec.ts):
 * - Usa todos os serviços reais (sem mocks de serviço)
 * - Intercepta apenas o transporte HTTP com HttpTestingController
 * - Verifica o ciclo completo de carregamento: convites → despesas → divisões (via HTTP chain)
 * - Testa criação de despesa coletiva com divisões paralelas (forkJoin)
 * - Testa marcar/desmarcar como pago e exclusão de despesa
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
import { provideRouter } from '@angular/router';
import { vi, describe, it, expect, beforeEach, afterEach } from 'vitest';

import { Despesas } from './despesas';
import { Despesa, Divisao } from '../core/models/despesas.model';
import { ConviteResponse } from '../core/services/convite.service';

const BASE         = 'http://localhost:8080';
const USUARIO_ID   = 1;
const ANFITRIAO_ID = 5;

// ── Factories ──────────────────────────────────────────────────────────────────

function makeDespesa(id: number, valor = 200): Despesa {
  return {
    id,
    valor,
    descricao: `Despesa ${id}`,
    dataVencimento: '2026-02-28T00:00:00Z',
    pago: [],
  };
}

function makeDivisao(id: number, despesaId: number, usuarioId: number, valor = 100): Divisao {
  return { id, despesaId, usuarioId, arquivoId: null, valor };
}

function makeConviteAceito(): ConviteResponse {
  return {
    id: 50,
    matchId: 10,
    anfitriaoId: ANFITRIAO_ID,
    colegaId: USUARIO_ID,
    status: 'ACEITO',
    criadoEm: '2026-01-01T00:00:00Z',
    respondidoEm: '2026-01-02T00:00:00Z',
    mensagem: null,
  };
}

describe('Despesas (integração)', () => {
  let component: Despesas;
  let fixture: ComponentFixture<Despesas>;
  let httpMock: HttpTestingController;

  const CONVITES_URL   = `${BASE}/chat/convite/listarPorUsuario/${USUARIO_ID}/COLEGA`;
  const DESPESAS_URL   = `${BASE}/despesas/listar`;
  const DIVISOES_URL   = (id: number) => `${BASE}/divisoes/despesa/${id}`;
  const CRIAR_DESP_URL = `${BASE}/despesas/criar`;
  const CRIAR_DIV_URL  = `${BASE}/divisoes/criar`;
  const PAGAR_URL      = (id: number) => `${BASE}/despesas/${id}/pagar/${USUARIO_ID}`;
  const DESMARCAR_URL  = (id: number) => `${BASE}/despesas/${id}/desmarcar/${USUARIO_ID}`;
  const EXCLUIR_URL    = (id: number) => `${BASE}/despesas/excluir/${id}`;

  beforeEach(async () => {
    sessionStorage.setItem('coliv_user_id', String(USUARIO_ID));
    sessionStorage.setItem('coliv_user_tipo', 'colega');

    await TestBed.configureTestingModule({
      imports: [Despesas],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    httpMock  = TestBed.inject(HttpTestingController);
    fixture   = TestBed.createComponent(Despesas);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  /** Substitui os métodos de dialog não implementados no JSDOM */
  function setupDialogStubs(): void {
    if (component.dialog?.nativeElement) {
      component.dialog.nativeElement.showModal = vi.fn();
      component.dialog.nativeElement.close     = vi.fn();
    }
    if (component.confirmDialog?.nativeElement) {
      component.confirmDialog.nativeElement.showModal = vi.fn();
      component.confirmDialog.nativeElement.close     = vi.fn();
    }
  }

  /**
   * Inicializa o componente e faz flush da cadeia HTTP padrão (colega):
   * GET convites → GET despesas → GET divisões (em paralelo via forkJoin)
   */
  function initWithDespesas(despesas: Despesa[], divisoesPorId: Map<number, Divisao[]>): void {
    fixture.detectChanges();
    setupDialogStubs();

    httpMock.expectOne(CONVITES_URL).flush([makeConviteAceito()]);
    httpMock.expectOne(DESPESAS_URL).flush(despesas);

    despesas.forEach(d => {
      httpMock.expectOne(DIVISOES_URL(d.id)).flush(divisoesPorId.get(d.id) ?? []);
    });
  }

  // ── Carregamento inicial ───────────────────────────────────────────────────

  it('deve carregar despesas via cadeia HTTP (convites → despesas → divisões)', () => {
    const divisoes1 = [
      makeDivisao(1, 1, ANFITRIAO_ID, 100),
      makeDivisao(2, 1, USUARIO_ID,   100),
    ];

    initWithDespesas([makeDespesa(1, 200)], new Map([[1, divisoes1]]));

    expect(component.despesas().length).toBe(1);
    expect(component.despesas()[0].nome).toBe('Despesa 1');
    expect(component.despesas()[0].valor).toBe(200);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBeNull();
  });

  it('deve filtrar despesas em que o usuário não tem divisão', () => {
    initWithDespesas(
      [makeDespesa(1, 100), makeDespesa(2, 200)],
      new Map([
        [1, [makeDivisao(1, 1, USUARIO_ID, 100)]],
        [2, [makeDivisao(2, 2, ANFITRIAO_ID, 200)]], // usuário não tem divisão aqui
      ]),
    );

    expect(component.despesas().length).toBe(1);
    expect(component.despesas()[0].id).toBe(1);
  });

  it('deve montar membrosDaCasa com anfitrião + colega ao encontrar convite aceito', () => {
    initWithDespesas(
      [makeDespesa(1)],
      new Map([[1, [makeDivisao(1, 1, USUARIO_ID)]]]),
    );

    expect(component.membrosDaCasa).toEqual([ANFITRIAO_ID, USUARIO_ID]);
  });

  it('deve calcular soma, totalPago e totalPendente corretamente', () => {
    const despesa: Despesa = { ...makeDespesa(1, 200), pago: [USUARIO_ID] };
    const divisoes = [
      makeDivisao(1, 1, ANFITRIAO_ID, 100),
      makeDivisao(2, 1, USUARIO_ID,   100),
    ];

    initWithDespesas([despesa], new Map([[1, divisoes]]));

    expect(component.soma()).toBe(200);
    expect(component.totalPago()).toBe(100);
    expect(component.totalPendente()).toBe(100);
  });

  it('deve apresentar lista vazia sem erro quando não há despesas cadastradas', () => {
    fixture.detectChanges();
    setupDialogStubs();

    httpMock.expectOne(CONVITES_URL).flush([makeConviteAceito()]);
    httpMock.expectOne(DESPESAS_URL).flush([]);

    expect(component.despesas().length).toBe(0);
    expect(component.carregando()).toBe(false);
    expect(component.erro()).toBeNull();
  });

  // ── Criar despesa coletiva ─────────────────────────────────────────────────

  it('deve criar despesa coletiva (POST) e depois as divisões (forkJoin) e recarregar lista', () => {
    initWithDespesas([], new Map());

    component.form.patchValue({
      nome:           'Aluguel',
      valor:          200,
      dataVencimento: '2026-02-01',
      tipodeDespesa:  'coletiva',
    });
    component.modoEdicao = false;
    component.enviarNovaDespesa();

    // POST despesa
    const criarReq = httpMock.expectOne(CRIAR_DESP_URL);
    expect(criarReq.request.method).toBe('POST');
    expect(criarReq.request.body.descricao).toBe('Aluguel');
    criarReq.flush(makeDespesa(10, 200));

    // POST divisões em paralelo via forkJoin (anfitrião + colega = 2 membros)
    const divReqs = httpMock.match(CRIAR_DIV_URL);
    expect(divReqs.length).toBe(2);
    divReqs.forEach((r, i) =>
      r.flush(makeDivisao(i + 1, 10, r.request.body.usuarioId, 100))
    );

    // Reload após criação
    httpMock.expectOne(DESPESAS_URL).flush([makeDespesa(10, 200)]);
    httpMock.expectOne(DIVISOES_URL(10)).flush([
      makeDivisao(1, 10, ANFITRIAO_ID, 100),
      makeDivisao(2, 10, USUARIO_ID,   100),
    ]);

    expect(component.despesas().length).toBe(1);
    expect(component.salvando()).toBe(false);
  });

  it('não deve chamar a API quando o formulário de despesa é inválido', () => {
    initWithDespesas([], new Map());

    component.form.patchValue({ nome: '', valor: 0 });
    component.enviarNovaDespesa();

    httpMock.expectNone(CRIAR_DESP_URL);
  });

  // ── Marcar/desmarcar como pago ─────────────────────────────────────────────

  it('deve chamar PATCH /pagar e atualizar o campo pago da despesa', () => {
    initWithDespesas(
      [makeDespesa(1, 200)],
      new Map([[1, [makeDivisao(1, 1, USUARIO_ID, 200)]]]),
    );

    component.togglePago(component.despesas()[0]);

    const pagarReq = httpMock.expectOne(PAGAR_URL(1));
    expect(pagarReq.request.method).toBe('PATCH');
    pagarReq.flush({ ...makeDespesa(1, 200), pago: [USUARIO_ID] });

    expect(component.despesas()[0].pago).toContain(USUARIO_ID);
  });

  it('deve chamar PATCH /desmarcar quando a despesa já consta como paga', () => {
    const despesa: Despesa = { ...makeDespesa(1, 200), pago: [USUARIO_ID] };

    initWithDespesas([despesa], new Map([[1, [makeDivisao(1, 1, USUARIO_ID, 200)]]]));

    component.togglePago(component.despesas()[0]);

    const desmarcarReq = httpMock.expectOne(DESMARCAR_URL(1));
    expect(desmarcarReq.request.method).toBe('PATCH');
    desmarcarReq.flush({ ...makeDespesa(1, 200), pago: [] });

    expect(component.despesas()[0].pago).not.toContain(USUARIO_ID);
  });

  // ── Excluir despesa ────────────────────────────────────────────────────────

  it('deve chamar DELETE e recarregar lista vazia após excluir despesa', () => {
    initWithDespesas(
      [makeDespesa(1, 200)],
      new Map([[1, [makeDivisao(1, 1, USUARIO_ID, 200)]]]),
    );

    component.despesaSelecionada = component.despesas()[0];
    component.excluirDespesa();

    const deleteReq = httpMock.expectOne(EXCLUIR_URL(1));
    expect(deleteReq.request.method).toBe('DELETE');
    deleteReq.flush(null);

    httpMock.expectOne(DESPESAS_URL).flush([]);

    expect(component.despesas().length).toBe(0);
  });

  it('deve exibir erro quando DELETE falha com 500', () => {
    initWithDespesas(
      [makeDespesa(1, 200)],
      new Map([[1, [makeDivisao(1, 1, USUARIO_ID)]]]),
    );

    component.despesaSelecionada = component.despesas()[0];
    component.excluirDespesa();

    httpMock
      .expectOne(EXCLUIR_URL(1))
      .flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(component.erro()).toBe('Erro interno no servidor. Tente novamente mais tarde.');
  });
});
