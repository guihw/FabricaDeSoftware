import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService, LoginResponse } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  const BASE = 'http://localhost:8080';

  const loginAnfitriaoMock: LoginResponse = { token: 'token-anfitriao', id: 5, tipo: 'ANFITRIAO' };
  const loginColegaMock: LoginResponse    = { token: 'token-colega',    id: 3, tipo: 'COLEGA'    };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AuthService],
    });
    service  = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });


  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  // ── login ────────────────────────────────────────────────────

  it('deve fazer POST para /auth/login com email e senha', () => {
    let resultado: LoginResponse | undefined;

    service.login({ email: 'rico@email.com', senha: 'senhaforte1' }).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'rico@email.com', senha: 'senhaforte1' });
    req.flush(loginAnfitriaoMock);

    expect(resultado).toEqual(loginAnfitriaoMock);
  });

  it('deve salvar token, id e tipo (em minúsculas) no sessionStorage após login de anfitrião', () => {
    service.login({ email: 'rico@email.com', senha: 'senhaforte1' }).subscribe();

    httpMock.expectOne(`${BASE}/auth/login`).flush(loginAnfitriaoMock);

    expect(sessionStorage.getItem('coliv_token')).toBe('token-anfitriao');
    expect(sessionStorage.getItem('coliv_user_id')).toBe('5');
    expect(sessionStorage.getItem('coliv_user_tipo')).toBe('anfitriao');
  });

  it('deve salvar coliv_user_tipo como "colega" após login de colega', () => {
    service.login({ email: 'lucas@email.com', senha: 'senha12345' }).subscribe();

    httpMock.expectOne(`${BASE}/auth/login`).flush(loginColegaMock);

    expect(sessionStorage.getItem('coliv_user_tipo')).toBe('colega');
  });

  it('deve atualizar isLoggedIn para true após login bem-sucedido', () => {
    expect(service.isLoggedIn()).toBe(false);

    service.login({ email: 'rico@email.com', senha: 'senhaforte1' }).subscribe();
    httpMock.expectOne(`${BASE}/auth/login`).flush(loginAnfitriaoMock);

    expect(service.isLoggedIn()).toBe(true);
  });

  it('não deve alterar sessionStorage nem isLoggedIn quando o login falha', () => {
    let erro: any;

    service.login({ email: 'rico@email.com', senha: 'errada' }).subscribe({
      error: (e) => (erro = e),
    });

    httpMock.expectOne(`${BASE}/auth/login`).flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(erro.status).toBe(401);
    expect(sessionStorage.getItem('coliv_token')).toBeNull();
    expect(sessionStorage.getItem('coliv_user_id')).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
  });

  // ── getToken / getUserId / getUserType ──────────────────────────

  it('getToken deve retornar null quando não há token salvo', () => {
    expect(service.getToken()).toBeNull();
  });

  it('getToken deve retornar o token salvo no sessionStorage', () => {
    sessionStorage.setItem('coliv_token', 'abc123');
    expect(service.getToken()).toBe('abc123');
  });

  it('getUserId deve retornar o id como número', () => {
    sessionStorage.setItem('coliv_user_id', '7');
    expect(service.getUserId()).toBe(7);
  });

  it('getUserId deve retornar null quando não há id salvo', () => {
    expect(service.getUserId()).toBeNull();
  });

  it('getUserType deve retornar o tipo salvo', () => {
    sessionStorage.setItem('coliv_user_tipo', 'colega');
    expect(service.getUserType()).toBe('colega');
  });

  it('getUserType deve retornar null quando não há tipo salvo', () => {
    expect(service.getUserType()).toBeNull();
  });

  // ── logout ───────────────────────────────────────────────────

  it('logout deve limpar token, id, tipo e isLoggedIn', () => {
    service.login({ email: 'rico@email.com', senha: 'senhaforte1' }).subscribe();
    httpMock.expectOne(`${BASE}/auth/login`).flush(loginAnfitriaoMock);

    service.logout();

    expect(sessionStorage.getItem('coliv_token')).toBeNull();
    expect(sessionStorage.getItem('coliv_user_id')).toBeNull();
    expect(sessionStorage.getItem('coliv_user_tipo')).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
  });

  // ── isLoggedIn ao inicializar (token persistido no sessionStorage) ──

  it('isLoggedIn deve ser true na criação se houver token JWT válido salvo', () => {
    const payload = { exp: Math.floor(Date.now() / 1000) + 3600 };
    const tokenValido = `header.${btoa(JSON.stringify(payload))}.assinatura`;
    sessionStorage.setItem('coliv_token', tokenValido);

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AuthService],
    });
    const novoService = TestBed.inject(AuthService);

    expect(novoService.isLoggedIn()).toBe(true);
  });

  it('isLoggedIn deve ser false na criação se o token estiver expirado', () => {
    const payload = { exp: Math.floor(Date.now() / 1000) - 3600 };
    const tokenExpirado = `header.${btoa(JSON.stringify(payload))}.assinatura`;
    sessionStorage.setItem('coliv_token', tokenExpirado);

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AuthService],
    });
    const novoService = TestBed.inject(AuthService);

    expect(novoService.isLoggedIn()).toBe(false);
  });

  it('isLoggedIn deve ser false na criação se o token estiver malformado', () => {
    sessionStorage.setItem('coliv_token', 'token-sem-formato-jwt');

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AuthService],
    });
    const novoService = TestBed.inject(AuthService);

    expect(novoService.isLoggedIn()).toBe(false);
  });

  it('isLoggedIn deve ser false na criação se não houver token salvo', () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), AuthService],
    });
    const novoService = TestBed.inject(AuthService);

    expect(novoService.isLoggedIn()).toBe(false);
  });
});