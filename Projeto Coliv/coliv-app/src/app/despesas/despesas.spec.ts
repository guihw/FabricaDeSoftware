import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { Despesas } from './despesas';
import { AuthService } from '../core/services/auth.service';
import { FotoPerfilService } from '../core/services/foto-perfil.service';
import { vi } from 'vitest';

describe('Despesas', () => {
  let component: Despesas;
  let fixture: ComponentFixture<Despesas>;

  // AuthService mockado
  const authServiceMock = {
    getUserId: vi.fn().mockReturnValue(1),
    getUserType: vi.fn().mockReturnValue('colega'),
    isLoggedIn: vi.fn().mockReturnValue(true),
    getToken: vi.fn().mockReturnValue(null),
  };

  const fotoPerfilServiceStub = {
    fotoPerfilUrl: signal<string | null>(null),
    hidratar: () => {},
    hidratarComId: () => {},
    cachear: () => {},
    limpar: () => {},
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Despesas],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
        { provide: FotoPerfilService, useValue: fotoPerfilServiceStub },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(Despesas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('deve ser criado', () => {
    expect(component).toBeTruthy();
  });
});