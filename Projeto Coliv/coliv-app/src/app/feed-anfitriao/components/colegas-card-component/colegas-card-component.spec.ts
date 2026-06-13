import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { ColegasCardComponent } from './colegas-card-component';
import { RecomendacaoColegaDTO } from '../../../core/services/recomendacao.service';

describe('ColegasCardComponent', () => {
  let component: ColegasCardComponent;
  let fixture: ComponentFixture<ColegasCardComponent>;

  const recomendacao: RecomendacaoColegaDTO = {
    colegaId: 3,
    nome: 'Ana Beatriz Costa',
    email: 'ana@email.com',
    score: 88,
    resumoCompatibilidade: 'Hábitos similares',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColegasCardComponent, CommonModule],
    }).compileComponents();

    fixture   = TestBed.createComponent(ColegasCardComponent);
    component = fixture.componentInstance;
    component.recomendacao = recomendacao;
    fixture.detectChanges();
  });

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve retornar score via getter', () => {
    expect(component.score).toBe(88);
  });

  it('deve retornar resumo via getter', () => {
    expect(component.resumo).toBe('Hábitos similares');
  });

  // ── iniciais ──────────────────────────────────────────────────

  it('deve retornar iniciais do nome (2 primeiras palavras)', () => {
    expect(component.iniciais).toBe('AB');
  });

  it('deve retornar inicial única para nome de uma palavra', () => {
    component.recomendacao = { ...recomendacao, nome: 'Carlos' };
    expect(component.iniciais).toBe('C');
  });

  it('deve retornar iniciais maiúsculas', () => {
    component.recomendacao = { ...recomendacao, nome: 'maria joana santos' };
    expect(component.iniciais).toBe('MJ');
  });

  // ── corScore ──────────────────────────────────────────────────

  it('deve retornar classe verde para score >= 80', () => {
    expect(component.corScore).toContain('secondary-container');
  });

  it('deve retornar classe primária para score 70', () => {
    component.recomendacao = { ...recomendacao, score: 70 };
    expect(component.corScore).toContain('primary-container');
  });

  it('deve retornar classe neutra para score abaixo de 60', () => {
    component.recomendacao = { ...recomendacao, score: 45 };
    expect(component.corScore).toContain('surface-container-high');
  });

  // ── Eventos ───────────────────────────────────────────────────

  it('deve emitir evento aceitar ao chamar onAceitar', () => {
    let emitido: RecomendacaoColegaDTO | undefined;
    component.aceitar.subscribe(r => (emitido = r));
    component.onAceitar();
    expect(emitido).toEqual(recomendacao);
  });

  it('deve emitir evento recusar ao chamar onRecusar', () => {
    let emitido: RecomendacaoColegaDTO | undefined;
    component.recusar.subscribe(r => (emitido = r));
    component.onRecusar();
    expect(emitido).toEqual(recomendacao);
  });
});