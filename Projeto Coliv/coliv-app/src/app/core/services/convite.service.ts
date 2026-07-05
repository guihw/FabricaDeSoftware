import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { ApiService } from './api.service';

export type StatusConvite = 'PENDENTE' | 'ACEITO' | 'RECUSADO' | 'CANCELADO';

// Espelha ConviteResponseDTO.java
export interface ConviteResponse {
  id: number;
  matchId: number;
  anfitriaoId: number;
  colegaId: number;
  status: StatusConvite;
  criadoEm: string;       // ISO-8601
  respondidoEm: string | null;
  mensagem: string | null;
}

// Corpo enviado ao criar convite
export interface ConviteRequestDTO {
  matchId: number;
  colegaId: number;
  mensagem?: string;
}


function normalizar(raw: any): ConviteResponse {
  return {
    id:           raw.id,
    matchId:      raw.matchId,
    anfitriaoId:  raw.anfitriaoId,
    colegaId:     raw.colegaId,
    // backend usa "conviteStatus"; front usa "status"
    status:       (raw.status ?? raw.conviteStatus) as StatusConvite,
    criadoEm:     raw.criadoEm ?? raw.dataInicio ?? new Date().toISOString(),
    respondidoEm: raw.respondidoEm ?? raw.dataAtualizacao ?? null,
    mensagem:     raw.mensagem ?? raw.texto ?? null,
  };
}

@Injectable({ providedIn: 'root' })
export class ConviteService extends ApiService {

  // ── Enviar convite ────────────────────────────────────────────
  enviar(anfitriaoId: number, dto: ConviteRequestDTO): Observable<ConviteResponse> {
    const body = { texto: dto.mensagem ?? null };
    return new Observable(subscriber => {
      this.post<any>(`/chat/convite/enviar/${dto.matchId}`, body).subscribe({
        next: raw  => { subscriber.next(normalizar(raw)); subscriber.complete(); },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── Aceitar convite ───────────────────────────────────────────
  aceitar(conviteId: number): Observable<ConviteResponse> {
    return new Observable(subscriber => {
      this.patch<any>(`/chat/convite/aceito/${conviteId}`).subscribe({
        next: () => {
          // Backend retorna void; reconstruímos buscando o convite atualizado
          this.get<any>(`/chat/convite/buscarConviteRecente/${conviteId}`).subscribe({
            next: raw  => { subscriber.next(normalizar(raw)); subscriber.complete(); },
            error: err => subscriber.error(err),
          });
        },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── Recusar convite ───────────────────────────────────────────
  recusar(conviteId: number): Observable<ConviteResponse> {
    return new Observable(subscriber => {
      this.patch<any>(`/chat/convite/recusado/${conviteId}`).subscribe({
        next: () => {
          this.get<any>(`/chat/convite/buscarConviteRecente/${conviteId}`).subscribe({
            next: raw  => { subscriber.next(normalizar(raw)); subscriber.complete(); },
            error: err => subscriber.error(err),
          });
        },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── Cancelar convite ──────────────────────────────────────────
  cancelar(conviteId: number): Observable<ConviteResponse> {
    return new Observable(subscriber => {
      this.patch<any>(`/chat/convite/cancelado/${conviteId}`).subscribe({
        next: () => {
          this.get<any>(`/chat/convite/buscarConviteRecente/${conviteId}`).subscribe({
            next: raw  => { subscriber.next(normalizar(raw)); subscriber.complete(); },
            error: err => subscriber.error(err),
          });
        },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── Listar para colega ────────────────────────────────────────
  listarParaColega(colegaId: number): Observable<ConviteResponse[]> {
    return new Observable(subscriber => {
      this.get<any[]>(`/chat/convite/listarPorUsuario/${colegaId}/COLEGA`).subscribe({
        next: list => { subscriber.next(list.map(normalizar)); subscriber.complete(); },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── Listar do anfitrião ───────────────────────────────────────
  listarDoAnfitriao(anfitriaoId: number): Observable<ConviteResponse[]> {
    return new Observable(subscriber => {
      this.get<any[]>(`/chat/convite/listarPorUsuario/${anfitriaoId}/ANFITRIAO`).subscribe({
        next: list => { subscriber.next(list.map(normalizar)); subscriber.complete(); },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── Buscar por matchId ────────────────────────────────────────
  buscarPorMatch(matchId: number): Observable<ConviteResponse | null> {
    return new Observable(subscriber => {
      this.get<any>(`/chat/convite/buscarConviteRecente/${matchId}`).subscribe({
        next: raw  => { subscriber.next(raw ? normalizar(raw) : null); subscriber.complete(); },
        // 404 = sem convite para esse match → retorna null em vez de erro
        error: err => {
          if (err.status === 404) { subscriber.next(null); subscriber.complete(); }
          else subscriber.error(err);
        },
      });
    });
  }

  buscarPorMatches(matchIds: number[]): Observable<ConviteResponse[]> {
    return new Observable(subscriber => {
      this.get<any[]>(`/chat/convite/buscarPorMatches?matchIds=${matchIds.join(',')}`).subscribe({
        next: list => { subscriber.next(list.map(normalizar)); subscriber.complete(); },
        error: err => subscriber.error(err),
      });
    });
  }

  // ── helper PATCH ──────────────────────────────────────────────
  protected override patch<T>(path: string, body?: unknown): Observable<T> {
    return this.http
      .patch<T>(`${this.baseUrl}${path}`, body ?? {});
  }
}