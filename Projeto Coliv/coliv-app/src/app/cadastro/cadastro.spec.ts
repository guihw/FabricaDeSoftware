import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { Cadastro } from './cadastro';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { ColegaService } from '../core/services/colega.service';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('Cadastro', () => {
  let component: Cadastro;
  let fixture: ComponentFixture<Cadastro>;
  let anfitriaoServiceSpy: jasmine.SpyObj<AnfitriaoService>;
  let colegaServiceSpy: jasmine.SpyObj<ColegaService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const anfitriaoMock = {
    id: 1, nome: 'Ricardo', cpf: '123.456.789-09',
    email: 'r@r.com', possuiPlano: false, fotoPerfil: null,
  };
  const colegaMock = { id: 2, nome: 'Lucas', email: 'l@l.com' };

  beforeEach(async () => {
    anfitriaoServiceSpy = jasmine.createSpyObj('AnfitriaoService', ['criar']);
    colegaServiceSpy = jasmine.createSpyObj('ColegaService', ['criar']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [Cadastro, ReactiveFormsModule, CommonModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AnfitriaoService, useValue: anfitriaoServiceSpy },
        { provide: ColegaService,    useValue: colegaServiceSpy    },
        { provide: Router,           useValue: routerSpy           },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Cadastro);
    component = fixture.componentInstance;
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
    expect(component.cadastroForm.get('nome')?.valid).toBeFalse();
  });

  it('deve validar o form quando todos os campos estão corretos', () => {
    component.cadastroForm.patchValue({
      nome: 'João Silva',
      cpf: '123.456.789-09',
      email: 'joao@email.com',
      senha: 'minhasenha123',
    });
    expect(component.cadastroForm.valid).toBeTrue();
  });

  it('deve invalidar email malformado', () => {
    component.cadastroForm.patchValue({ email: 'invalido' });
    expect(component.cadastroForm.get('email')?.valid).toBeFalse();
  });

  it('deve invalidar senha com menos de 8 caracteres', () => {
    component.cadastroForm.patchValue({ senha: '1234567' });
    expect(component.cadastroForm.get('senha')?.valid).toBeFalse();
  });



  it('campoInvalido deve retornar false quando o campo está intocado', () => {
    expect(component.campoInvalido('nome')).toBeFalse();
  });

  it('campoInvalido deve retornar true quando campo é tocado e inválido', () => {
    const ctrl = component.cadastroForm.get('nome')!;
    ctrl.markAsTouched();
    ctrl.setValue('AB');
    expect(component.campoInvalido('nome')).toBeTrue();
  });


  it('não deve chamar serviço se o formulário for inválido', () => {
    component.onSubmit();
    expect(anfitriaoServiceSpy.criar).not.toHaveBeenCalled();
    expect(colegaServiceSpy.criar).not.toHaveBeenCalled();
  });

  it('deve marcar todos os campos como tocados ao submeter form inválido', () => {
    component.onSubmit();
    expect(component.cadastroForm.get('nome')?.touched).toBeTrue();
  });


  it('deve chamar colegaService.criar quando perfil é colega', fakeAsync(() => {
    colegaServiceSpy.criar.and.returnValue(of(colegaMock));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    tick();
    expect(colegaServiceSpy.criar).toHaveBeenCalledWith(
      jasmine.objectContaining({ nome: 'Lucas Alves', email: 'lucas@email.com' })
    );
  }));

  it('deve salvar user_id e user_tipo no sessionStorage após cadastro de colega', fakeAsync(() => {
    colegaServiceSpy.criar.and.returnValue(of(colegaMock));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    tick();
    expect(sessionStorage.getItem('coliv_user_id')).toBe('2');
    expect(sessionStorage.getItem('coliv_user_tipo')).toBe('colega');
  }));

  it('deve navegar para /preferencias após cadastro de colega com sucesso', fakeAsync(() => {
    colegaServiceSpy.criar.and.returnValue(of(colegaMock));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas Alves', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    tick();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/preferencias']);
  }));


  it('deve chamar anfitriaoService.criar quando perfil é anfitriao', fakeAsync(() => {
    anfitriaoServiceSpy.criar.and.returnValue(of(anfitriaoMock));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silveira', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    tick();
    expect(anfitriaoServiceSpy.criar).toHaveBeenCalled();
  }));

  it('deve salvar user_tipo como "anfitriao" no sessionStorage', fakeAsync(() => {
    anfitriaoServiceSpy.criar.and.returnValue(of(anfitriaoMock));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silveira', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    tick();
    expect(sessionStorage.getItem('coliv_user_tipo')).toBe('anfitriao');
  }));


  it('deve exibir mensagem de erro quando cadastro de colega falha', fakeAsync(() => {
    const apiError = { status: 400, message: 'E-mail já cadastrado.' };
    colegaServiceSpy.criar.and.returnValue(throwError(() => apiError));
    component.selecionarPerfil('colega');
    component.cadastroForm.patchValue({
      nome: 'Lucas', cpf: '123.456.789-09',
      email: 'lucas@email.com', senha: 'senha12345',
    });
    component.onSubmit();
    tick();
    expect(component.erro()).toBe('E-mail já cadastrado.');
    expect(component.carregando()).toBeFalse();
  }));

  it('deve desativar carregando após erro de anfitrião', fakeAsync(() => {
    const apiError = { status: 500, message: 'Erro no servidor.' };
    anfitriaoServiceSpy.criar.and.returnValue(throwError(() => apiError));
    component.selecionarPerfil('anfitriao');
    component.cadastroForm.patchValue({
      nome: 'Ricardo Silva', cpf: '123.456.789-09',
      email: 'rico@email.com', senha: 'senhaforte1',
    });
    component.onSubmit();
    tick();
    expect(component.carregando()).toBeFalse();
  }));
});