import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

// Espelha: CardAnfitriaoResponseDTO no java
export interface CardAnfitriaoResponseDTO {
  anfitriaoId: number;
  nome: string;
  email: string;
  descricao: string;
  localizacao: string;
  quartos: number;
  classificacao: number | null;
  precoMensal: number;
  arquivos: string[];
  tipoVaga: string | null;
  comodidades: string[];
}

@Injectable({ providedIn: 'root' })
export class CardAnfitriaoService extends ApiService {
  private readonly PATH = '/cards/anfitriao';

  listarCardsCompletos(): Observable<CardAnfitriaoResponseDTO[]> {
    return this.get<CardAnfitriaoResponseDTO[]>(`${this.PATH}/card/info/listar`);
  }

  getCardInfo(anfitriaoId: number): Observable<CardAnfitriaoResponseDTO> {
    return this.get<CardAnfitriaoResponseDTO>(`${this.PATH}/card/info/${anfitriaoId}`);
  }

  editarPreco(anfitriaoId: number, precoMensal: number): Observable<{ precoMensal: string }> {
    return this.put<{ precoMensal: string }>(
      `${this.PATH}/editar/${anfitriaoId}`,
      { precoMensal: String(precoMensal) }
    );
  }

  atualizarArquivos(anfitriaoId: number, arquivoIds: number[]): Observable<void> {
    return this.put<void>(`${this.PATH}/${anfitriaoId}/arquivos`, arquivoIds);
  }
}