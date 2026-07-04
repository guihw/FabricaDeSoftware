import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { CardAnfitriaoResponseDTO } from './card-anfitriao.service';

export interface FeedPageDTO<T> {
  itens: T[];
  pagina: number;
  tamanhoPagina: number;
  totalItens: number;
  temProxima: boolean;
}

// Espelha: RecomendacaoCardAnfitriaoDTO.java
export interface RecomendacaoCardAnfitriaoDTO {
  card: CardAnfitriaoResponseDTO;   // já inclui tipoVaga e comodidades
  score: number;
  resumoCompatibilidade: string;
}

// Espelha: RecomendacaoColegaDTO.java
export interface RecomendacaoColegaDTO {
  colegaId: number;
  nome: string;
  email: string;
  score: number;
  resumoCompatibilidade: string;
  antecedentesVerificados?: boolean;
  fotoPerfil?: string | null;
}

@Injectable({ providedIn: 'root' })
export class RecomendacaoService extends ApiService {
  private readonly PATH = '/recomendacoes';

  feedColega(colegaId: number, pagina = 0, tamanho = 10): Observable<FeedPageDTO<RecomendacaoCardAnfitriaoDTO>> {
    return this.get<FeedPageDTO<RecomendacaoCardAnfitriaoDTO>>(
      `${this.PATH}/feed/colega/${colegaId}?pagina=${pagina}&tamanho=${tamanho}`
    );
  }

  feedAnfitriao(anfitriaoId: number, pagina = 0, tamanho = 10): Observable<FeedPageDTO<RecomendacaoColegaDTO>> {
    return this.get<FeedPageDTO<RecomendacaoColegaDTO>>(
      `${this.PATH}/feed/anfitriao/${anfitriaoId}?pagina=${pagina}&tamanho=${tamanho}`
    );
  }
}