import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type StatusConvite = 'PENDENTE' | 'ACEITO' | 'RECUSADO' | 'CANCELADO';


export interface ConviteResponse {
  id: number;
  matchId: number;
  anfitriaoId: number;
  colegaId: number;
  status: StatusConvite;
  criadoEm: string; // ISO-8601
  respondidoEm: string | null;
  mensagem: string | null;
}


export interface ConviteRequestDTO {
  matchId: number;
  colegaId: number;
  mensagem?: string;
}

@Injectable({ providedIn: 'root' })
export class ConviteService extends ApiService {
  private readonly PATH = '/convites';

  
  enviar(anfitriaoId: number, dto: ConviteRequestDTO): Observable<ConviteResponse> {
    return this.post<ConviteResponse>(`${this.PATH}/enviar/${anfitriaoId}`, dto);
  }

  /**
   * Colega aceita o convite.
   * PATCH /convites/{id}/aceitar
   */
  aceitar(conviteId: number): Observable<ConviteResponse> {
    return this.patch<ConviteResponse>(`${this.PATH}/${conviteId}/aceitar`);
  }


  recusar(conviteId: number): Observable<ConviteResponse> {
    return this.patch<ConviteResponse>(`${this.PATH}/${conviteId}/recusar`);
  }

 
  cancelar(conviteId: number): Observable<ConviteResponse> {
    return this.patch<ConviteResponse>(`${this.PATH}/${conviteId}/cancelar`);
  }

  /**
   * Lista todos os convites recebidos pelo colega.
   * GET /convites/colega/{colegaId}
   */
  listarParaColega(colegaId: number): Observable<ConviteResponse[]> {
    return this.get<ConviteResponse[]>(`${this.PATH}/colega/${colegaId}`);
  }

 
  listarDoAnfitriao(anfitriaoId: number): Observable<ConviteResponse[]> {
    return this.get<ConviteResponse[]>(`${this.PATH}/anfitriao/${anfitriaoId}`);
  }

  buscarPorMatch(matchId: number): Observable<ConviteResponse | null> {
    return this.get<ConviteResponse | null>(`${this.PATH}/match/${matchId}`);
  }


  protected patch<T>(path: string, body?: unknown): Observable<T> {
    return this.http
      .patch<T>(`${this.baseUrl}${path}`, body ?? {})
      .pipe((this as any).catchError ?? ((x: any) => x));
  }
}
