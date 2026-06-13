import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';
import { Chat } from './chat';
import { ConviteActionComponent } from './components/convite-action/convite-action';
import { ConviteService, ConviteResponse } from '../core/services/convite.service';

describe('Chat', () => {
  let component: Chat;
  let fixture: ComponentFixture<Chat>;
  let conviteServiceSpy: jasmine.SpyObj<ConviteService>;

  const activatedRouteMock = {
    snapshot: { paramMap: { get: (key: string) => (key === 'matchId' ? '10' : null) } },
  };

  const conviteMock: ConviteResponse = {
    id: 1, matchId: 10, anfitriaoId: 5, colegaId: 3,
    status: 'PENDENTE', criadoEm: '2025-01-01T00:00:00Z',
    respondidoEm: null, mensagem: null,
  };

  beforeEach(async () => {
    conviteServiceSpy = jasmine.createSpyObj('ConviteService', ['buscarPorMatch']);
    conviteServiceSpy.buscarPorMatch.and.returnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [Chat, CommonModule],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute,  useValue: activatedRouteMock },
        { provide: ConviteService,  useValue: conviteServiceSpy  },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(Chat);
    component = fixture.componentInstance;
  });

  afterEach(() => sessionStorage.clear());

  it('deve ser criado', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve ler matchId da rota', () => {
    fixture.detectChanges();
    expect(component.matchId).toBe(10);
  });

  it('deve identificar anfitrião pelo sessionStorage', () => {
    sessionStorage.setItem('coliv_user_tipo', 'anfitriao');
    sessionStorage.setItem('coliv_user_id', '5');
    fixture.detectChanges();
    expect(component.isAnfitriao).toBeTrue();
    expect(component.anfitriaoId).toBe(5);
  });

  it('deve identificar colega pelo sessionStorage', () => {
    sessionStorage.setItem('coliv_user_tipo', 'colega');
    sessionStorage.setItem('coliv_user_id', '3');
    fixture.detectChanges();
    expect(component.isAnfitriao).toBeFalse();
    expect(component.colegaId).toBe(3);
  });

  it('deve definir anfitriaoId a partir do coliv_chat_outro_id para colega', () => {
    sessionStorage.setItem('coliv_user_tipo', 'colega');
    sessionStorage.setItem('coliv_user_id', '3');
    sessionStorage.setItem('coliv_chat_outro_id', '5');
    fixture.detectChanges();
    expect(component.anfitriaoId).toBe(5);
  });

  it('deve definir colegaId a partir do coliv_chat_outro_id para anfitriao', () => {
    sessionStorage.setItem('coliv_user_tipo', 'anfitriao');
    sessionStorage.setItem('coliv_user_id', '5');
    sessionStorage.setItem('coliv_chat_outro_id', '3');
    fixture.detectChanges();
    expect(component.colegaId).toBe(3);
  });


  it('não deve definir notificação para convite null', () => {
    fixture.detectChanges();
    component.onConviteAtualizado(null);
    expect(component.notificacaoConvite).toBeNull();
  });

  it('deve exibir mensagem de convite PENDENTE', () => {
    fixture.detectChanges();
    component.onConviteAtualizado(conviteMock);
    expect(component.notificacaoConvite).toContain('Aguardando');
  });

  it('deve exibir mensagem de convite ACEITO', () => {
    fixture.detectChanges();
    component.onConviteAtualizado({ ...conviteMock, status: 'ACEITO' });
    expect(component.notificacaoConvite).toContain('despesas');
  });

  it('deve exibir mensagem de convite RECUSADO', () => {
    fixture.detectChanges();
    component.onConviteAtualizado({ ...conviteMock, status: 'RECUSADO' });
    expect(component.notificacaoConvite).toContain('recusou');
  });

  it('deve exibir mensagem de convite CANCELADO', () => {
    fixture.detectChanges();
    component.onConviteAtualizado({ ...conviteMock, status: 'CANCELADO' });
    expect(component.notificacaoConvite).toContain('cancelado');
  });

  it('deve limpar notificação após 6 segundos', (done) => {
    fixture.detectChanges();
    component.onConviteAtualizado(conviteMock);
    expect(component.notificacaoConvite).not.toBeNull();
    setTimeout(() => {
      expect(component.notificacaoConvite).toBeNull();
      done();
    }, 6100);
  });
});