import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { CriarAnuncio } from './criar-anuncio';
import { DadosImovelService } from '../core/services/dados-imovel.service';
import { DadosImovel } from '../core/models/formulario.model';

describe('CriarAnuncio', () => {
  let component: CriarAnuncio;
  let fixture: ComponentFixture<CriarAnuncio>;
  let dadosImovelSpy: { criar: ReturnType<typeof vi.fn> };

  const imovelMock: DadosImovel = {
    id: 1, anfitriaoId: 5, descricao: 'Casa bonita', localizacao: 'SP', quartos: 2,
  };

  beforeEach(async () => {
    dadosImovelSpy = { criar: vi.fn() };
    dadosImovelSpy.criar.mockReturnValue(of(imovelMock));

    sessionStorage.setItem('coliv_user_id', '5');

    await TestBed.configureTestingModule({
      imports: [CriarAnuncio, CommonModule, ReactiveFormsModule],
      providers: [{ provide: DadosImovelService, useValue: dadosImovelSpy }],
    }).compileComponents();

    fixture   = TestBed.createComponent(CriarAnuncio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());

  // ── Criação e formulário ──────────────────────────────────────

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve inicializar formulário com tipoVaga padrão', () => {
    expect(component.form.get('tipoVaga')?.value).toBe('Quarto Privativo');
  });

  it('formulário deve ser inválido com campos vazios', () => {
    expect(component.form.valid).toBe(false);
  });

  it('formulário deve ser válido com todos os campos preenchidos', () => {
    component.form.patchValue({
      manifesto: 'A'.repeat(50),
      preco: 1200,
      tipoVaga: 'Quarto Privativo',
      bairro: 'Pinheiros',
    });
    expect(component.form.valid).toBe(true);
  });

  // ── fotosPreenchidas ──────────────────────────────────────────

  it('fotosPreenchidas deve ser 0 inicialmente', () => {
    expect(component.fotosPreenchidas).toBe(0);
  });

  it('fotosPreenchidas deve incrementar quando foto é adicionada', () => {
    const file = new File([''], 'foto.jpg', { type: 'image/jpeg' });
    component.fotos[0] = { id: 0, arquivo: file, preview: 'data:image', principal: true };
    expect(component.fotosPreenchidas).toBe(1);
  });

  // ── prontoParaPublicar ────────────────────────────────────────

  it('prontoParaPublicar deve ser false sem fotos', () => {
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    expect(component.prontoParaPublicar).toBe(false);
  });

  it('prontoParaPublicar deve ser true com formulário válido e foto', () => {
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    component.fotos[0] = {
      id: 0,
      arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image',
      principal: true,
    };
    expect(component.prontoParaPublicar).toBe(true);
  });


  it('toggleAmenidade deve alternar estado de seleção', () => {
    const wifi = component.amenidades.find(a => a.id === 'wifi')!;
    const estadoInicial = wifi.selecionada;
    component.toggleAmenidade('wifi');
    expect(wifi.selecionada).toBe(!estadoInicial);
  });

  it('amenidadesSelecionadas deve retornar os ids selecionados', () => {
    // Garante que piscina não está selecionada
    component.amenidades.find(a => a.id === 'piscina')!.selecionada = false;
    const selecionados = component.amenidadesSelecionadas;
    expect(selecionados).not.toContain('piscina');
    // wifi começa como selecionado
    expect(selecionados).toContain('wifi');
  });

  it('não deve publicar se formulário inválido', fakeAsync(() => {
    component.publicar();
    tick();
    expect(dadosImovelSpy.criar).not.toHaveBeenCalled();
  }));

  it('não deve publicar sem foto', fakeAsync(() => {
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    component.publicar();
    tick();
    expect(dadosImovelSpy.criar).not.toHaveBeenCalled();
  }));

  it('deve chamar dadosImovelService.criar com o anfitriaoId da sessão', fakeAsync(() => {
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    component.fotos[0] = {
      id: 0,
      arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image',
      principal: true,
    };
    component.publicar();
    tick();
    expect(dadosImovelSpy.criar).toHaveBeenCalledWith(5, expect.any(Object));
  }));

  it('deve definir publicado como true após sucesso', fakeAsync(() => {
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    component.fotos[0] = {
      id: 0,
      arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image',
      principal: true,
    };
    component.publicar();
    tick();
    expect(component.publicado).toBe(true);
    expect(component.publicando).toBe(false);
  }));

  it('deve definir erroPublicacao quando publicar falha', fakeAsync(() => {
    dadosImovelSpy.criar.mockReturnValue(
      throwError(() => ({ status: 500, message: 'Erro no servidor.' }))
    );
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    component.fotos[0] = {
      id: 0,
      arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image',
      principal: true,
    };
    component.publicar();
    tick();
    expect(component.erroPublicacao).toBe('Erro no servidor.');
    expect(component.publicando).toBe(false);
  }));

  it('deve exibir erro de sessão quando não há anfitriaoId', fakeAsync(() => {
    sessionStorage.clear();
    component.form.patchValue({
      manifesto: 'A'.repeat(50), preco: 1200,
      tipoVaga: 'Quarto Privativo', bairro: 'Centro',
    });
    component.fotos[0] = {
      id: 0,
      arquivo: new File([''], 'foto.jpg', { type: 'image/jpeg' }),
      preview: 'data:image',
      principal: true,
    };
    component.publicar();
    tick();
    expect(component.erroPublicacao).toContain('Sessão');
    expect(dadosImovelSpy.criar).not.toHaveBeenCalled();
  }));


  it('voltarParaEdicao deve resetar publicado para false', () => {
    component.publicado = true;
    component.voltarParaEdicao();
    expect(component.publicado).toBe(false);
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
    const file1 = new File(['1'], 'foto1.jpg', { type: 'image/jpeg' });
    const file2 = new File(['2'], 'foto2.jpg', { type: 'image/jpeg' });
    component.fotos[0] = { id: 0, arquivo: file1, preview: 'preview1', principal: true };
    component.fotos[1] = { id: 1, arquivo: file2, preview: 'preview2', principal: false };

    component.onDragStart(0);
    component.onDrop(1);

    expect(component.fotos[0].preview).toBe('preview2');
    expect(component.fotos[1].preview).toBe('preview1');
  });

  it('onDrop com mesmo índice não deve trocar', () => {
    const file1 = new File(['1'], 'foto1.jpg', { type: 'image/jpeg' });
    component.fotos[0] = { id: 0, arquivo: file1, preview: 'preview1', principal: true };

    component.onDragStart(0);
    component.onDrop(0);

    expect(component.fotos[0].preview).toBe('preview1');
  });
});