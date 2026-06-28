import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, provideRouter } from '@angular/router';
import { CommonModule } from '@angular/common';
import { of, throwError } from 'rxjs';
import { describe, it, expect, vi  } from 'vitest';
import { Cadastro } from './cadastro';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { ColegaService } from '../core/services/colega.service';
import { AuthService } from '../core/services/auth.service';
import { AntecedentesService } from '../core/services/antecedentes.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('Cadastro', () => {
  let component: Cadastro;
  let fixture: ComponentFixture<Cadastro>;
  let anfitriaoServiceSpy: { criar: ReturnType<typeof vi.fn> };
  let colegaServiceSpy: { criar: ReturnType<typeof vi.fn> };
  let authServiceSpy: { login: ReturnType<typeof vi.fn>; getUserId: ReturnType<typeof vi.fn> };
  let antecedentesServiceSpy: { verificar: ReturnType<typeof vi.fn> };
  let routerSpy: Router;

  const anfitriaoMock = {
    id: 1, nome: 'Ricardo', cpf: '123.456.789-09',
    email: 'r@r.com', possuiPlano: false, fotoPerfil: null,
  };
  const colegaMock = { id: 2, nome: 'Lucas', email: 'l@l.com' };
  const loginResponseMock = { token: 'fake-jwt-token', id: 2, tipo: 'COLEGA' };

  beforeEach(async () => {
    anfitriaoServiceSpy = { criar: vi.fn() };
    colegaServiceSpy  = { criar: vi.fn() };
    authServiceSpy    = { login: vi.fn(), getUserId: vi.fn().mockReturnValue(2) };
    antecedentesServiceSpy = { verificar: vi.fn().mockReturnValue(of({})) };

    authServiceSpy.login.mockReturnValue(of(loginResponseMock));

    await TestBed.configureTestingModule({
      imports: [Cadastro, ReactiveFormsModule, CommonModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AnfitriaoService,    useValue: anfitriaoServiceSpy    },
        { provide: ColegaService,       useValue: colegaServiceSpy       },
        { provide: AuthService,         useValue: authServiceSpy         },
        { provide: AntecedentesService, useValue: antecedentesServiceSpy },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(Cadastro);
    component = fixture.componentInstance;
    routerSpy = TestBed.inject(Router);
    vi.spyOn(routerSpy, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());


  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve inicializar com perfil "colega" por padrão', () => {
    expect(component.perfil()).toBe('colega');
  });

  it('deve inicializar o formulário com campos vazios', () => {
    expect(component.cadastroForm.get('nome')?.value).toBe('');
    expect(component.cadastroForm.get('email')?.value).toBe('');
    expect(component.cadastroForm.get('senha')?.value).toBe('');
  });


  it('deve alternar perfil para "anfitriao"', () => {
    component.selecionarPerfil('anfitriao');
    expect(component.perfil()).toBe('anfitriao');
  });

  it('deve limpar erro ao selecionar perfil', () => {
    component.erro.set('Erro anterior');
    component.selecionarPerfil('colega');
    expect(component.erro()).toBeNull();
  });



  it('deve invalidar o form quando nome tem menos de 3 caracteres', () => {
    component.cadastroForm.patchValue({ nome: 'AB', email: 'a@a.com', senha: '12345678', cpf: '123.456.789-09' });
    expect(component.cadastroForm.get('nome')?.valid).toBe(false);
  });

  it('deve validar o form quando todos os campos estão corretos', () => {
    component.cadastroForm.patchValue({
      nome: 'João Silva',
      cpf: '123.456.789-09',
      email: 'joao@email.com',
      senha: 'minhasenha123',
    });
    expect(component.cadastroForm.valid).toBe(true);
  });

  it('deve invalidar email malformado', () => {
    component.cadastroForm.patchValue({ email: 'invalido' });
    expect(component.cadastroForm.get('email')?.valid).toBe(false);
  });

  it('deve invalidar senha com menos de 8 caracteres', () => {
    component.cadastroForm.patchValue({ senha: '1234567' });
    expect(component.cadastroForm.get('senha')?.valid).toBe(false);
  });



  it('campoInvalido deve retornar false quando o campo está intocado', () => {
    expect(component.campoInvalido('nome')).toBe(false);
  });

  it('campoInvalido deve retornar true quando campo é tocado e inválido', () => {
    const ctrl = component.cadastroForm.get('nome')!;
    ctrl.markAsTouched();
    ctrl.setValue('AB');
    expect(component.campoInvalido('nome')).toBe(true);
  });


  it('não deve chamar serviço se o formulário for inválido', () => {
    component.onSubmit();
    expect(anfitriaoServiceSpy.criar).not.toHaveBeenCalled();
    expect(colegaServiceSpy.criar).not.toHaveBeenCalled();
  });

  it('deve marcar todos os campos como tocados ao submeter form inválido', () => {
    component.onSubmit();
    expect(component.cadastroForm.get('nome')?.touched).toBe(true);
  });

  // Cadastro de Colega ----------------------------------------------------------

  it('deve chamar colegaService.criar quando perfil é colega', () => {
    colegaServiceSpy.criar.mockReturnValue(of(colegaMock));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    expect(colegaServiceSpy.criar).toHaveBeenCalledWith(
      expect.objectContaining({ nome: 'Lucas Alves', email: 'lucas@email.com' })
    );
  });

  it('deve chamar authService.login com as credenciais do colega após o cadastro', () => {
    colegaServiceSpy.criar.mockReturnValue(of(colegaMock));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    expect(authServiceSpy.login).toHaveBeenCalledWith({ email: 'lucas@email.com', senha: 'senha12345' });
  });

  it('deve navegar para /preferencias após cadastro de colega com sucesso', () => {
    colegaServiceSpy.criar.mockReturnValue(of(colegaMock));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/preferencias']);
  });

  //Cadastro de Anfitrião ----------------------------------------------------------

  it('deve chamar anfitriaoService.criar quando perfil é anfitriao', () => {
    anfitriaoServiceSpy.criar.mockReturnValue(of(anfitriaoMock));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silveira', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    expect(anfitriaoServiceSpy.criar).toHaveBeenCalled();
  });

  it('deve chamar authService.login com as credenciais do anfitrião após o cadastro', () => {
    anfitriaoServiceSpy.criar.mockReturnValue(of(anfitriaoMock));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silveira', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    expect(authServiceSpy.login).toHaveBeenCalledWith({ email: 'rico@email.com', senha: 'senhaforte1' });
  });

  it('deve navegar para /preferencias após cadastro de anfitrião com sucesso', () => {
    anfitriaoServiceSpy.criar.mockReturnValue(of(anfitriaoMock));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silveira', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/preferencias']);
  });

  // Aqui vai ser para tratar erros.

  it('deve exibir mensagem de erro quando cadastro de colega falha', () => {
    const apiError = { status: 400, message: 'E-mail já cadastrado.' };
    colegaServiceSpy.criar.mockReturnValue(throwError(() => apiError));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    expect(component.erro()).toBeTruthy();
    expect(component.carregando()).toBe(false);
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('deve desativar carregando após erro de anfitrião', () => {
    const apiError = { status: 500, message: 'Erro no servidor.' };
    anfitriaoServiceSpy.criar.mockReturnValue(throwError(() => apiError));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silva', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    expect(component.carregando()).toBe(false);
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('deve exibir erro quando o cadastro funciona mas o login automático falha', () => {
    colegaServiceSpy.criar.mockReturnValue(of(colegaMock));
    authServiceSpy.login.mockReturnValue(throwError(() => ({ status: 401, message: 'Erro ao autenticar.' })));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    expect(component.erro()).toBeTruthy();
    expect(component.carregando()).toBe(false);
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });
});