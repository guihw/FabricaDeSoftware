import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { CardAnfitriaoResponseDTO } from './card-anfitriao.service';

// ── DTOs espelhando o backend ─────────────────────────────────

// Espelha: FeedPageDTO.java
export interface FeedPageDTO<T> {
  itens: T[];
  pagina: number;
  tamanhoPagina: number;
  totalItens: number;
  temProxima: boolean;
}

// Espelha: RecomendacaoCardAnfitriaoDTO.java
export interface RecomendacaoCardAnfitriaoDTO {
  card: CardAnfitriaoResponseDTO;
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
}

@Injectable({ providedIn: 'root' })
export class RecomendacaoService extends ApiService {
  private readonly PATH = '/recomendacoes';

  /**
   * Feed de moradias recomendadas para um colega.
   * GET /recomendacoes/feed/colega/{colegaId}?pagina=0&tamanho=10
   */
  feedColega(
    colegaId: number,
    pagina = 0,
    tamanho = 10
  ): Observable<FeedPageDTO<RecomendacaoCardAnfitriaoDTO>> {
    return this.get<FeedPageDTO<RecomendacaoCardAnfitriaoDTO>>(
      `${this.PATH}/feed/colega/${colegaId}?pagina=${pagina}&tamanho=${tamanho}`
    );
  }

  /**
   * Feed de colegas recomendados para um anfitrião.
   * GET /recomendacoes/feed/anfitriao/{anfitriaoId}?pagina=0&tamanho=10
   */
  feedAnfitriao(
    anfitriaoId: number,
    pagina = 0,
    tamanho = 10
  ): Observable<FeedPageDTO<RecomendacaoColegaDTO>> {
    return this.get<FeedPageDTO<RecomendacaoColegaDTO>>(
      `${this.PATH}/feed/anfitriao/${anfitriaoId}?pagina=${pagina}&tamanho=${tamanho}`
    );
  }
}