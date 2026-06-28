import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, it, expect, vi } from 'vitest';
import { CriarAnuncio } from './criar-anuncio';
import { DadosImovelService } from '../core/services/dados-imovel.service';
import { DadosImovel } from '../core/models/formulario.model';
import { ArquivoService } from '../core/services/arquivo.service';
import { CardAnfitriaoService } from '../core/services/card-anfitriao.service';

describe('CriarAnuncio', () => {
  let component: CriarAnuncio;
  let fixture: ComponentFixture<CriarAnuncio>;
  let dadosImovelSpy: { criar: ReturnType<typeof vi.fn>; buscarPorAnfitriaoIdSeCompleto: ReturnType<typeof vi.fn> };
  let arquivoSpy: { upload: ReturnType<typeof vi.fn> };
  let cardAnfitriaoSpy: { atualizarArquivos: ReturnType<typeof vi.fn> };
  let routerSpy: Router;

  const imovelMock: DadosImovel = {
    id: 1, anfitriaoId: 5,
    descricao: 'Casa bonita', localizacao: 'Pinheiros, SP', quartos: 2,
    precoMensal: 2500, tipoVaga: 'Quarto Privativo', comodidades: ['wifi', 'pet'],
  };

  function preencherFormulario(component: CriarAnuncio, overrides: Record<string, any> = {}) {
    component.form.patchValue({
      manifesto: 'A'.repeat(50),
      preco: 2500,
      tipoVaga: 'Quarto Privativo',
      bairro: 'Pinheiros, SP',
      quartos: 2,
      ...overrides,
    });
  }

  beforeEach(async () => {
    dadosImovelSpy = {
      criar: vi.fn(),
      buscarPorAnfitriaoIdSeCompleto: vi.fn().mockReturnValue(of(null)),
    };
    dadosImovelSpy.criar.mockReturnValue(of(imovelMock));
    arquivoSpy = { upload: vi.fn().mockReturnValue(of([])) };
    cardAnfitriaoSpy = { atualizarArquivos: vi.fn().mockReturnValue(of(undefined)) };

    sessionStorage.setItem('coliv_user_id', '5');

    await TestBed.configureTestingModule({
      imports: [CriarAnuncio, CommonModule, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: DadosImovelService, useValue: dadosImovelSpy },
        { provide: ArquivoService, useValue: arquivoSpy },
        { provide: CardAnfitriaoService, useValue: cardAnfitriaoSpy },
      ],
    }).compileComponents();

    fixture   = TestBed.createComponent(CriarAnuncio);
    component = fixture.componentInstance;
    routerSpy = TestBed.inject(Router);
    vi.spyOn(routerSpy, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());


  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve inicializar tipoVaga com "Quarto Privativo"', () => {
    expect(component.form.get('tipoVaga')?.value).toBe('Quarto Privativo');
  });

  it('deve inicializar quartos com 1', () => {
    expect(component.form.get('quartos')?.value).toBe(1);
  });

  it('formulário deve ser inválido com campos vazios', () => {
    expect(component.form.valid).toBe(false);
  });

  it('formulário deve ser válido com todos os campos preenchidos', () => {
    preencherFormulario(component);
    expect(component.form.valid).toBe(true);
  });

  it('formulário deve ser inválido com preço zero', () => {
    preencherFormulario(component, { preco: 0 });
    expect(component.form.get('preco')?.valid).toBe(false);
  });

  it('formulário deve ser inválido com quartos zero', () => {
    preencherFormulario(component, { quartos: 0 });
    expect(component.form.get('quartos')?.valid).toBe(false);
  });

  it('formulário deve ser inválido com manifesto menor que 50 chars', () => {
    preencherFormulario(component, { manifesto: 'curto' });
    expect(component.form.get('manifesto')?.valid).toBe(false);
  });


  it('fotosPreenchidas deve ser 0 inicialmente', () => {
    expect(component.fotosPreenchidas).toBe(0);
  });

  it('fotosPreenchidas deve incrementar ao adicionar foto', () => {
    component.fotos[0] = {
      id: 0, arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image', principal: true,
    };
    expect(component.fotosPreenchidas).toBe(1);
  });

  it('prontoParaPublicar deve ser false sem foto', () => {
    preencherFormulario(component);
    expect(component.prontoParaPublicar).toBe(false);
  });

  it('prontoParaPublicar deve ser true com formulário válido e foto', () => {
    preencherFormulario(component);
    component.fotos[0] = {
      id: 0, arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image', principal: true,
    };
    expect(component.prontoParaPublicar).toBe(true);
  });


  it('toggleAmenidade deve alternar estado de seleção', () => {
    const wifi = component.amenidades.find(a => a.id === 'wifi')!;
    const antes = wifi.selecionada;
    component.toggleAmenidade('wifi');
    expect(wifi.selecionada).toBe(!antes);
  });

  it('amenidadesSelecionadas deve retornar só os ids selecionados', () => {
    component.amenidades.find(a => a.id === 'piscina')!.selecionada = false;
    expect(component.amenidadesSelecionadas).not.toContain('piscina');
    expect(component.amenidadesSelecionadas).toContain('wifi');
  });

  it('não deve publicar se formulário inválido', () => {
    component.publicar();
    expect(dadosImovelSpy.criar).not.toHaveBeenCalled();
  });

  it('não deve publicar sem foto', () => {
    preencherFormulario(component);
    component.publicar();
    expect(dadosImovelSpy.criar).not.toHaveBeenCalled();
  });

  it('deve chamar dadosImovelService.criar com todos os campos novos', () => {
    preencherFormulario(component);
    component.fotos[0] = {
      id: 0, arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image', principal: true,
    };
    component.amenidades.find(a => a.id === 'pet')!.selecionada = true;
    component.amenidades.find(a => a.id === 'wifi')!.selecionada = true;

    component.publicar();

    expect(dadosImovelSpy.criar).toHaveBeenCalledWith(
      5,
      expect.objectContaining({
        descricao:   'A'.repeat(50),
        localizacao: 'Pinheiros, SP',
        quartos:     2,
        precoMensal: 2500,
        tipoVaga:    'Quarto Privativo',
        comodidades: expect.arrayContaining(['wifi', 'pet']),
      })
    );
  });

  it('deve definir publicado como true após sucesso', () => {
    preencherFormulario(component);
    component.fotos[0] = {
      id: 0, arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image', principal: true,
    };
    component.publicar();
    expect(component.publicado).toBe(true);
    expect(component.publicando).toBe(false);
  });

  it('deve definir erroPublicacao quando publicar falha', () => {
    dadosImovelSpy.criar.mockReturnValue(
      throwError(() => ({ status: 500, message: 'Erro no servidor.' }))
    );
    preencherFormulario(component);
    component.fotos[0] = {
      id: 0, arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image', principal: true,
    };
    component.publicar();
    expect(component.erroPublicacao).toBe('Erro no servidor.');
    expect(component.publicando).toBe(false);
  });

  it('deve exibir erro de sessão quando não há anfitriaoId', () => {
    sessionStorage.clear();
    preencherFormulario(component);
    component.fotos[0] = {
      id: 0, arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image', principal: true,
    };
    component.publicar();
    expect(component.erroPublicacao).toContain('Sessão');
    expect(dadosImovelSpy.criar).not.toHaveBeenCalled();
  });

  // ── Helpers visuais ───────────────────────────────────────────

  it('voltarParaEdicao deve resetar publicado para false', () => {
    component.publicado = true;
    component.voltarParaEdicao();
    expect(component.publicado).toBe(false);
  });

  it('irParaGerenciarAnuncios deve navegar para /gerenciaranuncios', () => {
    component.irParaGerenciarAnuncios();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/gerenciaranuncios']);
  });

  it('campoInvalido deve retornar false quando intocado', () => {
    expect(component.campoInvalido('manifesto')).toBe(false);
  });

  it('campoInvalido deve retornar true quando tocado e inválido', () => {
    const ctrl = component.form.get('manifesto')!;
    ctrl.markAsTouched();
    ctrl.setValue('');
    expect(component.campoInvalido('manifesto')).toBe(true);
  });

  it('iconFill deve retornar FILL 1 quando selecionada', () => {
    expect(component.iconFill(true)).toBe("'FILL' 1");
  });

  it('iconFill deve retornar FILL 0 quando não selecionada', () => {
    expect(component.iconFill(false)).toBe("'FILL' 0");
  });


  it('onDrop deve trocar fotos entre slots', () => {
    component.fotos[0] = { id: 0, arquivo: new File(['1'], 'f1.jpg'), preview: 'p1', principal: true };
    component.fotos[1] = { id: 1, arquivo: new File(['2'], 'f2.jpg'), preview: 'p2', principal: false };
    component.onDragStart(0);
    component.onDrop(1);
    expect(component.fotos[0].preview).toBe('p2');
    expect(component.fotos[1].preview).toBe('p1');
  });

  it('onDrop com mesmo índice não deve trocar', () => {
    component.fotos[0] = { id: 0, arquivo: new File(['1'], 'f1.jpg'), preview: 'p1', principal: true };
    component.onDragStart(0);
    component.onDrop(0);
    expect(component.fotos[0].preview).toBe('p1');
  });
});