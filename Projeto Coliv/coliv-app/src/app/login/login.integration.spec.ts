/**
 * Testes de integração: Login + AuthService
 *
 * Diferencial em relação aos testes unitários existentes (login.spec.ts):
 * - Usa AuthService real (não mockado) injetado via DI
 * - Intercepta apenas o transporte HTTP com HttpTestingController
 * - Verifica o ciclo completo: formulário → POST HTTP → sessionStorage → sinal reativo → navegação
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

import { Login } from './login';
import { AuthService } from '../core/services/auth.service';

const BASE = 'http://localhost:8080';

describe('Login (integração)', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let httpMock: HttpTestingController;
  let router: Router;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    httpMock    = TestBed.inject(HttpTestingController);
    router      = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);
    fixture     = TestBed.createComponent(Login);
    component   = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('deve enviar POST para /auth/login com os dados do formulário', () => {
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();

    const req = httpMock.expectOne(`${BASE}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'lucas@email.com', senha: 'senha12345' });
    req.flush({ token: 'jwt-colega', id: 3, tipo: 'COLEGA' });
  });

  it('deve persistir token, id e tipo no sessionStorage após login bem-sucedido', () => {
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();

    httpMock
      .expectOne(`${BASE}/auth/login`)
      .flush({ token: 'jwt-colega', id: 3, tipo: 'COLEGA' });

    expect(sessionStorage.getItem('coliv_token')).toBe('jwt-colega');
    expect(sessionStorage.getItem('coliv_user_id')).toBe('3');
    expect(sessionStorage.getItem('coliv_user_tipo')).toBe('colega');
  });

  it('deve atualizar AuthService.isLoggedIn() para true após login bem-sucedido', () => {
    expect(authService.isLoggedIn()).toBe(false);

    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();
    httpMock.expectOne(`${BASE}/auth/login`).flush({ token: 'jwt-colega', id: 3, tipo: 'COLEGA' });

    expect(authService.isLoggedIn()).toBe(true);
  });

  it('deve navegar para /feedcolega após login de colega', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();
    httpMock.expectOne(`${BASE}/auth/login`).flush({ token: 'jwt-colega', id: 3, tipo: 'COLEGA' });

    expect(navigateSpy).toHaveBeenCalledWith(['/feedcolega']);
  });

  it('deve navegar para /feedanfitriao após login de anfitrião', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.loginForm.patchValue({ email: 'rico@email.com', senha: 'senhaforte1' });
    component.onSubmit();
    httpMock.expectOne(`${BASE}/auth/login`).flush({ token: 'jwt-anf', id: 5, tipo: 'ANFITRIAO' });

    expect(navigateSpy).toHaveBeenCalledWith(['/feedanfitriao']);
  });

  it('não deve fazer requisição HTTP quando o formulário é inválido', () => {
    component.loginForm.patchValue({ email: 'invalido', senha: '' });
    component.onSubmit();

    httpMock.expectNone(`${BASE}/auth/login`);
  });

  it('deve exibir mensagem de erro e não salvar token após 401', () => {
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'errada' });
    component.onSubmit();

    httpMock
      .expectOne(`${BASE}/auth/login`)
      .flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(component.erro()).toBe('E-mail ou senha inválidos.');
    expect(component.carregando()).toBe(false);
    expect(sessionStorage.getItem('coliv_token')).toBeNull();
    expect(authService.isLoggedIn()).toBe(false);
  });

  it('deve exibir erro quando servidor retorna 500', () => {
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();

    httpMock
      .expectOne(`${BASE}/auth/login`)
      .flush({}, { status: 500, statusText: 'Internal Server Error' });

    expect(component.erro()).toBeTruthy();
    expect(component.carregando()).toBe(false);
  });
});
