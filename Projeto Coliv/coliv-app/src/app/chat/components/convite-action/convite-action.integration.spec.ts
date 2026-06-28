/**
 * Testes de integração: ConviteActionComponent + ConviteService
 *
 * Diferencial em relação ao teste unitário existente:
 * - Usa ConviteService real (sem mock de serviço)
 * - Intercepta apenas o transporte HTTP com HttpTestingController
 * - Verifica o ciclo completo de convite: carregar → enviar → aceitar/recusar/cancelar
 * - Testa as chamadas duplas (PATCH + GET) que ocorrem em aceitar/recusar/cancelar
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
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

import { ConviteActionComponent } from './convite-action';
import { ConviteService, ConviteResponse } from '../../../core/services/convite.service';

const BASE     = 'http://localhost:8080';
const MATCH_ID = 42;

function makeConvite(status: ConviteResponse['status'] = 'PENDENTE'): ConviteResponse {
  return {
    id: 10,
    matchId: MATCH_ID,
    anfitriaoId: 5,
    colegaId: 3,
    status,
    criadoEm: '2026-01-01T10:00:00Z',
    respondidoEm: null,
    mensagem: null,
  };
}

describe('ConviteActionComponent (integração)', () => {
  let component: ConviteActionComponent;
  let fixture: ComponentFixture<ConviteActionComponent>;
  let httpMock: HttpTestingController;

  const BUSCAR_URL        = `${BASE}/chat/convite/buscarConviteRecente/${MATCH_ID}`;
  const ENVIAR_URL        = `${BASE}/chat/convite/enviar/${MATCH_ID}`;
  const ACEITAR_PATCH_URL = `${BASE}/chat/convite/aceito/${MATCH_ID}`;
  const RECUSAR_PATCH_URL = `${BASE}/chat/convite/recusado/${MATCH_ID}`;
  const CANCELAR_PATCH_URL = `${BASE}/chat/convite/cancelado/${MATCH_ID}`;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConviteActionComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        ConviteService,
      ],
    }).compileComponents();

    httpMock  = TestBed.inject(HttpTestingController);
    fixture   = TestBed.createComponent(ConviteActionComponent);
    component = fixture.componentInstance;

    component.matchId     = MATCH_ID;
    component.isAnfitriao = true;
    component.anfitriaoId = 5;
    component.colegaId    = 3;
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ── Carregamento inicial ────────────────────────────────────────

  it('deve carregar convite existente via GET em ngOnInit', () => {
    fixture.detectChanges();

    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('PENDENTE'));

    expect(component.convite).not.toBeNull();
    expect(component.convite?.status).toBe('PENDENTE');
    expect(component.convite?.id).toBe(10);
  });

  it('deve definir convite como null quando a API retorna 404', () => {
    fixture.detectChanges();

    httpMock
      .expectOne(BUSCAR_URL)
      .flush({}, { status: 404, statusText: 'Not Found' });

    expect(component.convite).toBeNull();
  });

  it('deve emitir conviteAtualizado com o convite carregado', () => {
    const emitidos: (ConviteResponse | null)[] = [];
    component.conviteAtualizado.subscribe(c => emitidos.push(c));

    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('PENDENTE'));

    expect(emitidos.length).toBe(1);
    expect(emitidos[0]?.status).toBe('PENDENTE');
  });

  // ── Enviar convite ──────────────────────────────────────────────

  it('deve enviar POST para /chat/convite/enviar/:matchId ao chamar onEnviar()', () => {
    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush(null);

    component.onEnviar();

    const req = httpMock.expectOne(ENVIAR_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ texto: null });
    req.flush(makeConvite('PENDENTE'));

    expect(component.convite?.status).toBe('PENDENTE');
    expect(component.enviando).toBe(false);
  });

  it('deve definir erro e limpar enviando quando POST de convite falha', () => {
    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush(null);

    component.onEnviar();
    httpMock
      .expectOne(ENVIAR_URL)
      .flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(component.erro).toBe('Erro interno no servidor. Tente novamente mais tarde.');
    expect(component.enviando).toBe(false);
  });

  // ── Aceitar convite ─────────────────────────────────────────────

  it('deve fazer PATCH + GET ao aceitar convite e atualizar status para ACEITO', () => {
    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('PENDENTE'));

    component.onAceitar();

    httpMock.expectOne(ACEITAR_PATCH_URL).flush({});
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('ACEITO'));

    expect(component.convite?.status).toBe('ACEITO');
    expect(component.respondendo).toBe(false);
  });

  // ── Recusar convite ─────────────────────────────────────────────

  it('deve fazer PATCH + GET ao recusar convite e atualizar status para RECUSADO', () => {
    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('PENDENTE'));

    component.onRecusar();

    httpMock.expectOne(RECUSAR_PATCH_URL).flush({});
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('RECUSADO'));

    expect(component.convite?.status).toBe('RECUSADO');
    expect(component.respondendo).toBe(false);
  });

  // ── Cancelar convite ────────────────────────────────────────────

  it('deve fazer PATCH + GET ao cancelar convite e atualizar status para CANCELADO', () => {
    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('PENDENTE'));

    component.onCancelar();

    httpMock.expectOne(CANCELAR_PATCH_URL).flush({});
    httpMock.expectOne(BUSCAR_URL).flush(makeConvite('CANCELADO'));

    expect(component.convite?.status).toBe('CANCELADO');
    expect(component.cancelando).toBe(false);
  });

  it('deve normalizar resposta do backend com campo conviteStatus em vez de status', () => {
    fixture.detectChanges();
    httpMock.expectOne(BUSCAR_URL).flush({
      id: 10,
      matchId: MATCH_ID,
      anfitriaoId: 5,
      colegaId: 3,
      conviteStatus: 'ACEITO',
      criadoEm: '2026-01-01T10:00:00Z',
    });

    expect(component.convite?.status).toBe('ACEITO');
  });
});
