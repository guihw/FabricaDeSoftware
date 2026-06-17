import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, provideRouter } from '@angular/router';
import { CommonModule } from '@angular/common';
import { of, throwError } from 'rxjs';
import { describe, it, expect, vi } from 'vitest';
import { Login } from './login';
import { AuthService } from '../core/services/auth.service';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceSpy: { login: ReturnType<typeof vi.fn> };
  let routerSpy: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    authServiceSpy = { login: vi.fn() };
    routerSpy = { navigate: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [Login, ReactiveFormsModule, CommonModule],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve inicializar o formulário com campos vazios', () => {
    expect(component.loginForm.get('email')?.value).toBe('');
    expect(component.loginForm.get('senha')?.value).toBe('');
  });

  it('deve invalidar email malformado', () => {
    component.loginForm.patchValue({ email: 'invalido', senha: '12345678' });
    expect(component.loginForm.get('email')?.valid).toBe(false);
  });

  it('não deve chamar authService.login se o formulário for inválido', () => {
    component.onSubmit();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('deve marcar campos como tocados ao submeter form inválido', () => {
    component.onSubmit();
    expect(component.loginForm.get('email')?.touched).toBe(true);
  });

  it('deve chamar authService.login com email e senha corretos', fakeAsync(() => {
    authServiceSpy.login.mockReturnValue(of({ token: 'abc', id: 1, tipo: 'COLEGA' }));
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();
    tick();
    expect(authServiceSpy.login).toHaveBeenCalledWith({ email: 'lucas@email.com', senha: 'senha12345' });
  }));

  it('deve navegar para /feedcolega quando tipo é COLEGA', fakeAsync(() => {
    authServiceSpy.login.mockReturnValue(of({ token: 'abc', id: 1, tipo: 'COLEGA' }));
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'senha12345' });
    component.onSubmit();
    tick();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/feedcolega']);
  }));

  it('deve navegar para /feedanfitriao quando tipo é ANFITRIAO', fakeAsync(() => {
    authServiceSpy.login.mockReturnValue(of({ token: 'abc', id: 5, tipo: 'ANFITRIAO' }));
    component.loginForm.patchValue({ email: 'rico@email.com', senha: 'senhaforte1' });
    component.onSubmit();
    tick();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/feedanfitriao']);
  }));

  it('deve exibir mensagem de erro quando login falha', fakeAsync(() => {
    authServiceSpy.login.mockReturnValue(
      throwError(() => ({ status: 401, message: 'Sem permissão para realizar esta ação.' }))
    );
    component.loginForm.patchValue({ email: 'lucas@email.com', senha: 'errada123' });
    component.onSubmit();
    tick();
    expect(component.erro()).toBe('E-mail ou senha inválidos.');
    expect(component.carregando()).toBe(false);
  }));

  it('toggleSenha deve alternar mostrarSenha', () => {
    expect(component.mostrarSenha()).toBe(false);
    component.toggleSenha();
    expect(component.mostrarSenha()).toBe(true);
  });

  it('campoInvalido deve retornar true quando campo é tocado e inválido', () => {
    const ctrl = component.loginForm.get('email')!;
    ctrl.markAsTouched();
    ctrl.setValue('invalido');
    expect(component.campoInvalido('email')).toBe(true);
  });
});