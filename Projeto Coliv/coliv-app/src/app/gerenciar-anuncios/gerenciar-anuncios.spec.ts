import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { signal } from '@angular/core';
import { GerenciarAnuncios } from './gerenciar-anuncios';
import { CardAnfitriaoService, CardAnfitriaoResponseDTO } from '../core/services/card-anfitriao.service';
import { FotoPerfilService } from '../core/services/foto-perfil.service';

const fotoPerfilServiceStub = {
  fotoPerfilUrl: signal<string | null>(null),
  hidratar: () => {},
  hidratarComId: () => {},
  cachear: () => {},
  limpar: () => {},
};

describe('GerenciarAnuncios', () => {
  let component: GerenciarAnuncios;
  let fixture: ComponentFixture<GerenciarAnuncios>;
  let cardServiceSpy: { getCardInfo: ReturnType<typeof vi.fn> };

  const cardCompleto: CardAnfitriaoResponseDTO = {
    anfitriaoId: 5,
    nome: 'Casa Pinheiros',
    email: 'anf@email.com',
    descricao: 'Casa espaçosa',
    localizacao: 'Pinheiros, SP',
    quartos: 3,
    classificacao: 4.5,
    precoMensal: 2500,
    arquivos: [],
    tipoVaga: null,
    comodidades: [],
  };

  const cardIncompleto: CardAnfitriaoResponseDTO = {
    ...cardCompleto,
    descricao: '',
    localizacao: '',
  };

  beforeEach(async () => {
    cardServiceSpy = { getCardInfo: vi.fn() };
    cardServiceSpy.getCardInfo.mockReturnValue(of(cardCompleto));

    sessionStorage.setItem('coliv_user_id', '5');

    await TestBed.configureTestingModule({
      imports: [GerenciarAnuncios, CommonModule],
      providers: [
        provideRouter([]),
        { provide: CardAnfitriaoService, useValue: cardServiceSpy },
        { provide: FotoPerfilService, useValue: fotoPerfilServiceStub },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GerenciarAnuncios);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());

  it('deve ser criado', () => expect(component).toBeTruthy());

  it('deve chamar getCardInfo com o id do sessionStorage', () => {
    expect(cardServiceSpy.getCardInfo).toHaveBeenCalledWith(5);
  });

  it('deve popular o anúncio após carregamento', () => {
    expect(component.anuncio()?.nome).toBe('Casa Pinheiros');
  });

  it('deve definir carregando como false após sucesso', () => {
    expect(component.carregando()).toBe(false);
  });

  it('deve considerar status ativo quando descricao, localizacao e preco estão preenchidos', () => {
    expect(component.status()).toBe('ativo');
  });

  it('deve considerar status rascunho quando faltam dados', () => {
    cardServiceSpy.getCardInfo.mockReturnValue(of(cardIncompleto));
    component.carregar();
    expect(component.status()).toBe('rascunho');
  });

  it('deve formatar o preço em BRL', () => {
    expect(component.precoFormatado()).toContain('2.500');
  });

  it('deve tratar 404 como "sem anúncio ainda" (não como erro)', () => {
    cardServiceSpy.getCardInfo.mockReturnValue(
      throwError(() => ({ status: 404, message: 'Recurso não encontrado.' }))
    );
    component.carregar();
    expect(component.anuncio()).toBeNull();
    expect(component.erro()).toBeNull();
    expect(component.carregando()).toBe(false);
  });

  it('deve exibir mensagem de erro quando getCardInfo falha com erro genérico', () => {
    cardServiceSpy.getCardInfo.mockReturnValue(
      throwError(() => ({ status: 500, message: 'Erro interno no servidor.' }))
    );
    component.carregar();
    expect(component.erro()).toBe('Erro interno no servidor.');
    expect(component.carregando()).toBe(false);
  });

  it('deve exibir erro de sessão quando não há user_id no sessionStorage', () => {
    sessionStorage.clear();
    fixture = TestBed.createComponent(GerenciarAnuncios);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.erro()).toContain('Sessão');
    expect(component.carregando()).toBe(false);
    expect(cardServiceSpy.getCardInfo).not.toHaveBeenCalledWith(NaN);
  });
});