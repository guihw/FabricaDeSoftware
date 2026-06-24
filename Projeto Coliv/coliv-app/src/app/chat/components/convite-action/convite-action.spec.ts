import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ConviteActionComponent } from './convite-action';
import { ConviteService, ConviteResponse } from '../../../core/services/convite.service';

describe('ConviteActionComponent', () => {
  let component: ConviteActionComponent;
  let fixture: ComponentFixture<ConviteActionComponent>;
  let conviteServiceSpy: {
    buscarPorMatch: ReturnType<typeof vi.fn>;
    enviar: ReturnType<typeof vi.fn>;
    cancelar: ReturnType<typeof vi.fn>;
    aceitar: ReturnType<typeof vi.fn>;
    recusar: ReturnType<typeof vi.fn>;
  };

  const MATCH_ID = 10;

  const convitePendente: ConviteResponse = {
    id: 1, matchId: MATCH_ID, anfitriaoId: 5, colegaId: 3,
    status: 'PENDENTE', criadoEm: '2025-01-01T00:00:00Z',
    respondidoEm: null, mensagem: null,
  };

  const conviteAceito: ConviteResponse = { ...convitePendente, status: 'ACEITO' };
  const conviteRecusado: ConviteResponse = { ...convitePendente, status: 'RECUSADO' };
  const conviteCancelado: ConviteResponse = { ...convitePendente, status: 'CANCELADO' };

  function setup(isAnfitriao = true) {
    component.matchId = MATCH_ID;
    component.isAnfitriao = isAnfitriao;
    component.anfitriaoId = 5;
    component.colegaId = 3;
    fixture.detectChanges();
  }

  beforeEach(async () => {
    conviteServiceSpy = {
      buscarPorMatch: vi.fn().mockReturnValue(of(null)),
      enviar: vi.fn(),
      cancelar: vi.fn(),
      aceitar: vi.fn(),
      recusar: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ConviteActionComponent, CommonModule],
      providers: [
        provideRouter([]),
        { provide: ConviteService, useValue: conviteServiceSpy },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(ConviteActionComponent);
    component = fixture.componentInstance;
  });

  // ── Inicialização ─────────────────────────────────────────────

  it('deve ser criado', () => { setup(); expect(component).toBeTruthy(); });

  it('deve chamar buscarPorMatch com o matchId fornecido', () => {
    setup();
    expect(conviteServiceSpy.buscarPorMatch).toHaveBeenCalledWith(MATCH_ID);
  });

  it('deve definir convite como null quando não há convite existente', () => {
    setup();
    expect(component.convite).toBeNull();
  });

  it('deve emitir conviteAtualizado ao carregar convite existente', () => {
    conviteServiceSpy.buscarPorMatch.mockReturnValue(of(convitePendente));
    let emitido: ConviteResponse | null | undefined;
    component.matchId = MATCH_ID; component.isAnfitriao = true;
    component.anfitriaoId = 5; component.colegaId = 3;
    component.conviteAtualizado.subscribe(c => (emitido = c));
    fixture.detectChanges();
    expect(emitido).toEqual(convitePendente);
  });

  // ── onEnviar ──────────────────────────────────────────────────

  it('deve chamar conviteService.enviar com matchId e colegaId', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(of(convitePendente));
    setup();
    component.onEnviar();
    tick();
    expect(conviteServiceSpy.enviar).toHaveBeenCalledWith(5, {
      matchId: MATCH_ID,
      colegaId: 3,
      mensagem: undefined,
    });
  }));

  it('deve atualizar convite após envio com sucesso', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(of(convitePendente));
    setup();
    component.onEnviar();
    tick();
    expect(component.convite).toEqual(convitePendente);
    expect(component.enviando).toBe(false);
  }));

  it('deve definir erro quando onEnviar falha', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(
      throwError(() => ({ message: 'Falha ao enviar.' }))
    );
    setup();
    component.onEnviar();
    tick();
    expect(component.erro).toBe('Falha ao enviar.');
    expect(component.enviando).toBe(false);
  }));

  it('não deve chamar enviar se já está enviando', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(of(convitePendente));
    setup();
    component.enviando = true;
    component.onEnviar();
    tick();
    expect(conviteServiceSpy.enviar).not.toHaveBeenCalled();
  }));

  it('deve cancelar usando matchId — não convite.id', fakeAsync(() => {
    conviteServiceSpy.cancelar.mockReturnValue(of(conviteCancelado));
    setup();
    component.convite = convitePendente;
    component.onCancelar();
    tick();
    expect(conviteServiceSpy.cancelar).toHaveBeenCalledWith(MATCH_ID);
    expect(component.convite?.status).toBe('CANCELADO');
    expect(component.cancelando).toBe(false);
  }));

  it('não deve cancelar se não há convite', fakeAsync(() => {
    setup();
    component.convite = null;
    component.onCancelar();
    tick();
    expect(conviteServiceSpy.cancelar).not.toHaveBeenCalled();
  }));

  it('deve definir erro quando cancelar falha', fakeAsync(() => {
    conviteServiceSpy.cancelar.mockReturnValue(
      throwError(() => ({ message: 'Erro ao cancelar.' }))
    );
    setup();
    component.convite = convitePendente;
    component.onCancelar();
    tick();
    expect(component.erro).toBe('Erro ao cancelar.');
  }));


  it('deve aceitar usando matchId — não convite.id', fakeAsync(() => {
    conviteServiceSpy.aceitar.mockReturnValue(of(conviteAceito));
    setup(false); // colega
    component.convite = convitePendente;
    component.onAceitar();
    tick();
    // CORREÇÃO: deve chamar com MATCH_ID (10), não convite.id (1)
    expect(conviteServiceSpy.aceitar).toHaveBeenCalledWith(MATCH_ID);
    expect(component.convite?.status).toBe('ACEITO');
    expect(component.respondendo).toBe(false);
  }));

  it('não deve aceitar se já está respondendo', fakeAsync(() => {
    setup(false);
    component.convite    = convitePendente;
    component.respondendo = true;
    component.onAceitar();
    tick();
    expect(conviteServiceSpy.aceitar).not.toHaveBeenCalled();
  }));

  it('deve definir erro quando aceitar falha', fakeAsync(() => {
    conviteServiceSpy.aceitar.mockReturnValue(
      throwError(() => ({ message: 'Erro ao aceitar.' }))
    );
    setup(false);
    component.convite = convitePendente;
    component.onAceitar();
    tick();
    expect(component.erro).toBe('Erro ao aceitar.');
  }));


  it('deve recusar usando matchId — não convite.id', fakeAsync(() => {
    conviteServiceSpy.recusar.mockReturnValue(of(conviteRecusado));
    setup(false);
    component.convite = convitePendente;
    component.onRecusar();
    tick();
    expect(conviteServiceSpy.recusar).toHaveBeenCalledWith(MATCH_ID);
    expect(component.convite?.status).toBe('RECUSADO');
  }));

  it('não deve recusar se não há convite', fakeAsync(() => {
    setup(false);
    component.convite = null;
    component.onRecusar();
    tick();
    expect(conviteServiceSpy.recusar).not.toHaveBeenCalled();
  }));


  it('deve emitir conviteAtualizado ao aceitar', fakeAsync(() => {
    conviteServiceSpy.aceitar.mockReturnValue(of(conviteAceito));
    setup(false);
    component.convite = convitePendente;
    let emitido: ConviteResponse | null | undefined;
    component.conviteAtualizado.subscribe(c => (emitido = c));
    component.onAceitar();
    tick();
    expect(emitido?.status).toBe('ACEITO');
  }));

  it('deve emitir conviteAtualizado ao cancelar', fakeAsync(() => {
    conviteServiceSpy.cancelar.mockReturnValue(of(conviteCancelado));
    setup();
    component.convite = convitePendente;
    let emitido: ConviteResponse | null | undefined;
    component.conviteAtualizado.subscribe(c => (emitido = c));
    component.onCancelar();
    tick();
    expect(emitido?.status).toBe('CANCELADO');
  }));
});