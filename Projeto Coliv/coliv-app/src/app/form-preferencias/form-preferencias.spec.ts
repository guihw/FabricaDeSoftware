import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { FormPreferencias } from './form-preferencias';
import { PreferenciasAnfitriaoService } from '../core/services/preferencias-anfitriao.service';
import { PreferenciasColegaService } from '../core/services/preferencias-colega.service';

describe('FormPreferencias', () => {
  let component: FormPreferencias;
  let fixture: ComponentFixture<FormPreferencias>;
  let anfitriaoService: { criar: ReturnType<typeof vi.fn> };
  let colegaService: { criar: ReturnType<typeof vi.fn> };
  let router: Router;

  const prefAnfitriaoMock = {
    id: 1, anfitriaoId: 5,
    presencaAnimais: false, horariosParaVisita: '14:00:00',
    politicaDeLimpeza: 'Limpo', regrasDaCasa: 'Silêncio às 22h',
    perfilColegaDesejado: 'Profissional',
  };

  const prefColegaMock = {
    id: 2,
    precoMinimo: 800, precoMaximo: 2000,
    localizacao: 'SP',
    horarioDeSono: '23:00:00',
    nivelDeSociabilidade: 'MODERADO' as const,
    nivelDeLimpeza: 'MEDIO' as const,
    habitoDeTrabalho: 'HIBRIDO' as const,
    aceitaAnimais: false,
  };

  function setupSession(tipo: 'anfitriao' | 'colega', id = 5) {
    sessionStorage.setItem('coliv_user_tipo', tipo);
    sessionStorage.setItem('coliv_user_id', String(id));
  }

  beforeEach(async () => {
    anfitriaoService = { criar: vi.fn() };
    colegaService    = { criar: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [FormPreferencias, CommonModule, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: PreferenciasAnfitriaoService, useValue: anfitriaoService },
        { provide: PreferenciasColegaService,    useValue: colegaService    },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(FormPreferencias);
    component = fixture.componentInstance;
    router    = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  afterEach(() => sessionStorage.clear());



  it('deve ser criado', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve detectar tipo "anfitriao" pelo sessionStorage', () => {
    setupSession('anfitriao');
    fixture.detectChanges();
    expect(component.isAnfitriao()).toBe(true);
  });

  it('deve detectar tipo "colega" pelo sessionStorage', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component.isAnfitriao()).toBe(false);
  });

  it('deve carregar userId do sessionStorage', () => {
    setupSession('colega', 42);
    fixture.detectChanges();
    expect(component.userId()).toBe(42);
  });

  it('deve inicializar com ritmo EQUILIBRADO', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component.ritmoSono()).toBe('EQUILIBRADO');
  });

  it('deve inicializar com sociabilidade MODERADO', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component.sociabilidade()).toBe('MODERADO');
  });


  it('ativo() deve retornar true quando valor coincide', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component.ativo(component.ritmoSono, 'EQUILIBRADO')).toBe(true);
  });

  it('ativo() deve retornar false quando valor não coincide', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component.ativo(component.ritmoSono, 'NOTURNO')).toBe(false);
  });

  it('togglePets deve alternar o estado de aceitaPets', () => {
    setupSession('colega');
    fixture.detectChanges();
    expect(component.aceitaPets()).toBe(false);
    component.togglePets();
    expect(component.aceitaPets()).toBe(true);
    component.togglePets();
    expect(component.aceitaPets()).toBe(false);
  });


  it('salvar deve marcar campos como tocados se formulário inválido', () => {
    setupSession('colega');
    fixture.detectChanges();
    component.form.patchValue({ localizacao: '' });
    component.salvar();
    expect(component.form.get('localizacao')?.touched).toBe(true);
  });

  it('deve exibir erro quando não há userId na sessão', () => {
    sessionStorage.clear();
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP' });
    component.salvar();
    expect(component.erro()).toContain('Sessão');
  });

  // ── Salvar Colega ─────────────────────────────────────────────

  it('deve chamar colegaService.criar para perfil colega', () => {
    colegaService.criar.mockReturnValue(of(prefColegaMock));
    setupSession('colega', 3);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP', precoMin: 800, precoMax: 2000 });
    component.salvar();
    expect(colegaService.criar).toHaveBeenCalledWith(3, expect.objectContaining({
      localizacao: 'SP', precoMinimo: 800, precoMaximo: 2000,
    }));
  });

  it('deve navegar para /feedcolega após salvar colega com sucesso', () => {
    vi.useFakeTimers();
    colegaService.criar.mockReturnValue(of(prefColegaMock));
    setupSession('colega', 3);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP', precoMin: 800, precoMax: 2000 });
    component.salvar();
    vi.runAllTimers();
    expect(router.navigate).toHaveBeenCalledWith(['/feedcolega']);
    vi.useRealTimers();
  });

  it('deve definir erro quando precoMin > precoMax', () => {
    setupSession('colega', 3);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP', precoMin: 3000, precoMax: 1000 });
    component.salvar();
    expect(component.erro()).toContain('mínimo');
    expect(colegaService.criar).not.toHaveBeenCalled();
  });

  it('deve exibir erro quando colegaService.criar falha', () => {
    colegaService.criar.mockReturnValue(
      throwError(() => ({ status: 400, message: 'Dados inválidos.' }))
    );
    setupSession('colega', 3);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP', precoMin: 800, precoMax: 2000 });
    component.salvar();
    expect(component.erro()).toBe('Dados inválidos.');
    expect(component.carregando()).toBe(false);
  });

  // ── Salvar Anfitrião ──────────────────────────────────────────

  it('deve chamar anfitriaoService.criar para perfil anfitriao', () => {
    anfitriaoService.criar.mockReturnValue(of(prefAnfitriaoMock));
    setupSession('anfitriao', 5);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP' });
    component.salvar();
    expect(anfitriaoService.criar).toHaveBeenCalledWith(5, expect.any(Object));
  });

  it('deve navegar para /criaranuncio após salvar anfitrião com sucesso', () => {
    vi.useFakeTimers();
    anfitriaoService.criar.mockReturnValue(of(prefAnfitriaoMock));
    setupSession('anfitriao', 5);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP' });
    component.salvar();
    vi.runAllTimers();
    expect(router.navigate).toHaveBeenCalledWith(['/criaranuncio']);
    vi.useRealTimers();
  });

  it('deve definir sucesso como true após salvar anfitrião', () => {
    anfitriaoService.criar.mockReturnValue(of(prefAnfitriaoMock));
    setupSession('anfitriao', 5);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP' });
    component.salvar();
    expect(component.sucesso()).toBe(true);
  });

  it('deve incluir aceitaAnimais no dto do colega', () => {
    colegaService.criar.mockReturnValue(of(prefColegaMock));
    setupSession('colega', 3);
    fixture.detectChanges();
    component.form.patchValue({ localizacao: 'SP', precoMin: 800, precoMax: 2000 });
    component.aceitaPets.set(true);
    component.salvar();
    expect(colegaService.criar).toHaveBeenCalledWith(3,
      expect.objectContaining({ aceitaAnimais: true })
    );
  });
});