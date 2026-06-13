import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { MoradiaCardComponent } from './moradia-card-component';
import { RecomendacaoCardAnfitriaoDTO } from '../../../core/services/recomendacao.service';

describe('MoradiaCardComponent', () => {
  let component: MoradiaCardComponent;
  let fixture: ComponentFixture<MoradiaCardComponent>;

  const recomendacaoBase: RecomendacaoCardAnfitriaoDTO = {
    score: 75,
    resumoCompatibilidade: 'Perfil compatível',
    card: {
      anfitriaoId: 1,
      nome: 'Casa Pinheiros',
      email: 'anf@email.com',
      descricao: 'Casa espaçosa',
      localizacao: 'Pinheiros, SP',
      quartos: 3,
      classificacao: 4.2,
      precoMensal: 2500,
      arquivos: [],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoradiaCardComponent, CommonModule],
    }).compileComponents();

    fixture   = TestBed.createComponent(MoradiaCardComponent);
    component = fixture.componentInstance;
    component.recomendacao = recomendacaoBase;
    fixture.detectChanges();
  });

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve retornar o card correto via getter', () => {
    expect(component.card).toBe(recomendacaoBase.card);
  });

  it('deve retornar o score via getter', () => {
    expect(component.score).toBe(75);
  });

  it('deve retornar o resumo via getter', () => {
    expect(component.resumo).toBe('Perfil compatível');
  });

  // ── precoFormatado ─────────────────────────────────────────────

  it('deve formatar preço em BRL corretamente', () => {
    expect(component.precoFormatado).toContain('2.500');
  });

  it('deve retornar "A consultar" quando preço é 0', () => {
    component.recomendacao = {
      ...recomendacaoBase,
      card: { ...recomendacaoBase.card, precoMensal: 0 },
    };
    expect(component.precoFormatado).toBe('A consultar');
  });

  // ── corScore ──────────────────────────────────────────────────

  it('deve retornar classe verde para score >= 80', () => {
    component.recomendacao = { ...recomendacaoBase, score: 90 };
    expect(component.corScore).toContain('secondary-container');
  });

  it('deve retornar classe primária para score entre 60 e 79', () => {
    expect(component.corScore).toContain('primary-container');
  });

  it('deve retornar classe neutra para score < 60', () => {
    component.recomendacao = { ...recomendacaoBase, score: 40 };
    expect(component.corScore).toContain('surface-container-high');
  });

  // ── estrelas ──────────────────────────────────────────────────

  it('estrelas(4) deve retornar true para classificação 4.2', () => {
    expect(component.estrelas(4)).toBe(true);
  });

  it('estrelas(5) deve retornar false para classificação 4.2', () => {
    expect(component.estrelas(5)).toBe(false);
  });

  it('estrelas deve retornar false quando classificação é null', () => {
    component.recomendacao = {
      ...recomendacaoBase,
      card: { ...recomendacaoBase.card, classificacao: null },
    };
    expect(component.estrelas(1)).toBe(false);
  });

  it('deve emitir evento like com a recomendação ao clicar', () => {
    let emitido: RecomendacaoCardAnfitriaoDTO | undefined;
    component.like.subscribe(r => (emitido = r));
    component.onLike();
    expect(emitido).toEqual(recomendacaoBase);
  });
});