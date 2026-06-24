import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface MatchResponse {
  id: number;
  colegaId: number;
  anfitriaoId: number;
  status: 'PENDENTE' | 'ACEITO' | 'CANCELADO';
}

@Injectable({ providedIn: 'root' })
export class MatchService extends ApiService {

  criar(colegaId: number, anfitriaoId: number): Observable<MatchResponse> {
    return this.post<MatchResponse>('/matches/novo', {
      iniciador: 'COLEGA',
      colegaId,
      anfitriaoId,
    });
  }

  criarAceito(colegaId: number, anfitriaoId: number): Observable<MatchResponse> {
    return this.post<MatchResponse>(`/matches/${colegaId}/${anfitriaoId}/aceitar`, {});
  }

  buscar(id: number): Observable<MatchResponse> {
    return this.get<MatchResponse>(`/matches/${id}`);
  }

  cancelar(id: number): Observable<void> {
    return this.delete<void>(`/matches/${id}`);
  }
}