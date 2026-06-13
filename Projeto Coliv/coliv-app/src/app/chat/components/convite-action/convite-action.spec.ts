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

  const convitePendente: ConviteResponse = {
    id: 1, matchId: 10, anfitriaoId: 5, colegaId: 3,
    status: 'PENDENTE', criadoEm: '2025-01-01T00:00:00Z',
    respondidoEm: null, mensagem: null,
  };

  const conviteAceito: ConviteResponse    = { ...convitePendente, status: 'ACEITO'    };
  const conviteRecusado: ConviteResponse  = { ...convitePendente, status: 'RECUSADO'  };
  const conviteCancelado: ConviteResponse = { ...convitePendente, status: 'CANCELADO' };

  function configureComponent(isAnfitriao = true) {
    component.matchId     = 10;
    component.isAnfitriao = isAnfitriao;
    component.anfitriaoId = 5;
    component.colegaId    = 3;
    fixture.detectChanges();
  }

  beforeEach(async () => {
    conviteServiceSpy = {
      buscarPorMatch: vi.fn(),
      enviar: vi.fn(),
      cancelar: vi.fn(),
      aceitar: vi.fn(),
      recusar: vi.fn(),
    };
    // Padrão: sem convite existente
    conviteServiceSpy.buscarPorMatch.mockReturnValue(of(null));

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

  it('deve ser criado', () => {
    configureComponent();
    expect(component).toBeTruthy();
  });

  it('deve chamar buscarPorMatch no ngOnInit com o matchId fornecido', () => {
    configureComponent();
    expect(conviteServiceSpy.buscarPorMatch).toHaveBeenCalledWith(10);
  });

  it('deve definir convite como null quando não há convite existente', () => {
    configureComponent();
    expect(component.convite).toBeNull();
  });

  it('deve emitir conviteAtualizado ao carregar convite existente', () => {
    conviteServiceSpy.buscarPorMatch.mockReturnValue(of(convitePendente));
    let emitido: ConviteResponse | null | undefined;
    component.matchId = 10; component.isAnfitriao = true;
    component.anfitriaoId = 5; component.colegaId = 3;
    component.conviteAtualizado.subscribe(c => (emitido = c));
    fixture.detectChanges();
    expect(emitido).toEqual(convitePendente);
  });

  // ── onEnviar ──────────────────────────────────────────────────

  it('deve chamar conviteService.enviar ao executar onEnviar', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(of(convitePendente));
    configureComponent();
    component.onEnviar();
    tick();
    expect(conviteServiceSpy.enviar).toHaveBeenCalledWith(5, {
      matchId: 10, colegaId: 3, mensagem: undefined,
    });
  }));

  it('deve atualizar convite após envio com sucesso', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(of(convitePendente));
    configureComponent();
    component.onEnviar();
    tick();
    expect(component.convite).toEqual(convitePendente);
    expect(component.enviando).toBe(false);
  }));

  it('deve definir erro e desativar enviando quando onEnviar falha', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(
      throwError(() => ({ message: 'Falha ao enviar.' }))
    );
    configureComponent();
    component.onEnviar();
    tick();
    expect(component.erro).toBe('Falha ao enviar.');
    expect(component.enviando).toBe(false);
  }));

  it('não deve chamar enviar duas vezes se já está enviando', fakeAsync(() => {
    conviteServiceSpy.enviar.mockReturnValue(of(convitePendente));
    configureComponent();
    component.enviando = true;
    component.onEnviar();
    tick();
    expect(conviteServiceSpy.enviar).not.toHaveBeenCalled();
  }));

  // ── onCancelar ────────────────────────────────────────────────

  it('deve cancelar convite pendente', fakeAsync(() => {
    conviteServiceSpy.cancelar.mockReturnValue(of(conviteCancelado));
    configureComponent();
    component.convite = convitePendente;
    component.onCancelar();
    tick();
    expect(component.convite?.status).toBe('CANCELADO');
    expect(component.cancelando).toBe(false);
  }));

  it('não deve chamar cancelar se não há convite', fakeAsync(() => {
    configureComponent();
    component.convite = null;
    component.onCancelar();
    tick();
    expect(conviteServiceSpy.cancelar).not.toHaveBeenCalled();
  }));

  it('deve definir erro quando cancelar falha', fakeAsync(() => {
    conviteServiceSpy.cancelar.mockReturnValue(
      throwError(() => ({ message: 'Erro ao cancelar.' }))
    );
    configureComponent();
    component.convite = convitePendente;
    component.onCancelar();
    tick();
    expect(component.erro).toBe('Erro ao cancelar.');
  }));

  // ── onAceitar ─────────────────────────────────────────────────

  it('deve aceitar convite e atualizar status para ACEITO', fakeAsync(() => {
    conviteServiceSpy.aceitar.mockReturnValue(of(conviteAceito));
    configureComponent(false); // colega
    component.convite = convitePendente;
    component.onAceitar();
    tick();
    expect(component.convite?.status).toBe('ACEITO');
    expect(component.respondendo).toBe(false);
  }));

  it('não deve aceitar se já está respondendo', fakeAsync(() => {
    configureComponent(false);
    component.convite = convitePendente;
    component.respondendo = true;
    component.onAceitar();
    tick();
    expect(conviteServiceSpy.aceitar).not.toHaveBeenCalled();
  }));

  it('deve definir erro quando aceitar falha', fakeAsync(() => {
    conviteServiceSpy.aceitar.mockReturnValue(
      throwError(() => ({ message: 'Erro ao aceitar.' }))
    );
    configureComponent(false);
    component.convite = convitePendente;
    component.onAceitar();
    tick();
    expect(component.erro).toBe('Erro ao aceitar.');
  }));

  // ── onRecusar ─────────────────────────────────────────────────

  it('deve recusar convite e atualizar status para RECUSADO', fakeAsync(() => {
    conviteServiceSpy.recusar.mockReturnValue(of(conviteRecusado));
    configureComponent(false);
    component.convite = convitePendente;
    component.onRecusar();
    tick();
    expect(component.convite?.status).toBe('RECUSADO');
  }));

  it('não deve recusar se não há convite', fakeAsync(() => {
    configureComponent(false);
    component.convite = null;
    component.onRecusar();
    tick();
    expect(conviteServiceSpy.recusar).not.toHaveBeenCalled();
  }));

  // ── Emissão de eventos ────────────────────────────────────────

  it('deve emitir conviteAtualizado ao aceitar', fakeAsync(() => {
    conviteServiceSpy.aceitar.mockReturnValue(of(conviteAceito));
    configureComponent(false);
    component.convite = convitePendente;
    let emitido: ConviteResponse | null | undefined;
    component.conviteAtualizado.subscribe(c => (emitido = c));
    component.onAceitar();
    tick();
    expect(emitido?.status).toBe('ACEITO');
  }));

  it('deve emitir conviteAtualizado ao cancelar', fakeAsync(() => {
    conviteServiceSpy.cancelar.mockReturnValue(of(conviteCancelado));
    configureComponent();
    component.convite = convitePendente;
    let emitido: ConviteResponse | null | undefined;
    component.conviteAtualizado.subscribe(c => (emitido = c));
    component.onCancelar();
    tick();
    expect(emitido?.status).toBe('CANCELADO');
  }));
});