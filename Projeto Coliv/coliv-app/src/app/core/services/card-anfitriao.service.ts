import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

// Espelha: CardAnfitriaoResponseDTO.java
export interface CardAnfitriaoResponseDTO {
  anfitriaoId: number;
  nome: string;
  email: string;
  descricao: string;
  localizacao: string;
  quartos: number;
  classificacao: number | null;
  precoMensal: number;
  arquivos: number[];
}

@Injectable({ providedIn: 'root' })
export class CardAnfitriaoService extends ApiService {
  private readonly PATH = '/cards/anfitriao';

  // GET /cards/anfitriao/card/info/listar
  listarCardsCompletos(): Observable<CardAnfitriaoResponseDTO[]> {
    return this.get<CardAnfitriaoResponseDTO[]>(`${this.PATH}/card/info/listar`);
  }

  // GET /cards/anfitriao/card/info/{anfitriaoId}
  getCardInfo(anfitriaoId: number): Observable<CardAnfitriaoResponseDTO> {
    return this.get<CardAnfitriaoResponseDTO>(`${this.PATH}/card/info/${anfitriaoId}`);
  }
}