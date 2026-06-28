import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { vi } from 'vitest';
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
      nome: 'Ricardo Silveira',       
      email: 'anf@email.com',        
      descricao: 'Casa espaçosa',
      localizacao: 'Pinheiros, SP',
      quartos: 3,
      classificacao: 4.2,
      precoMensal: 2500,
      arquivos: [],
      tipoVaga: 'Quarto Privativo',
      comodidades: ['wifi', 'pet', 'academia', 'piscina'], // 4 comodidades — badge mostra até 3
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


  it('titulo deve ser "tipoVaga, localização" — não o nome do anfitrião', () => {
    expect(component.titulo).toBe('Quarto Privativo, Pinheiros, SP');
    expect(component.titulo).not.toContain('Ricardo Silveira');
  });

  it('titulo deve usar apenas tipoVaga quando localizacao for vazia', () => {
    component.recomendacao = {
      ...recomendacaoBase,
      card: { ...recomendacaoBase.card, localizacao: '' },
    };
    expect(component.titulo).toBe('Quarto Privativo');
  });

  it('titulo deve usar "Imóvel" quando tipoVaga for null', () => {
    component.recomendacao = {
      ...recomendacaoBase,
      card: { ...recomendacaoBase.card, tipoVaga: null },
    };
    expect(component.titulo).toBe('Imóvel, Pinheiros, SP');
  });


  it('badgesComodidades deve retornar no máximo 3 itens', () => {
    // recomendacaoBase tem 4 comodidades
    expect(component.badgesComodidades.length).toBeLessThanOrEqual(3);
  });

  it('badgesComodidades deve priorizar pet, wifi, academia sobre piscina', () => {
    const badges = component.badgesComodidades;
    expect(badges).toContain('Pet OK');
    expect(badges).toContain('Wi-Fi');
    expect(badges).not.toContain('Piscina'); // 4ª na prioridade, cortada no limite de 3
  });

  it('badgesComodidades deve retornar array vazio quando não há comodidades', () => {
    component.recomendacao = {
      ...recomendacaoBase,
      card: { ...recomendacaoBase.card, comodidades: [] },
    };
    expect(component.badgesComodidades).toHaveLength(0);
  });

  it('badgesComodidades não deve incluir e-mail do anfitrião', () => {
    expect(component.badgesComodidades).not.toContain('anf@email.com');
  });


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

  it('deve retornar "A consultar" quando preço é null', () => {
    component.recomendacao = {
      ...recomendacaoBase,
      card: { ...recomendacaoBase.card, precoMensal: null as any },
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


  it('deve emitir evento like com a recomendação ao chamar onLike', () => {
    let emitido: RecomendacaoCardAnfitriaoDTO | undefined;
    component.like.subscribe(r => (emitido = r));
    component.onLike({ stopPropagation: vi.fn() } as unknown as MouseEvent);
    expect(emitido).toEqual(recomendacaoBase);
  });
});