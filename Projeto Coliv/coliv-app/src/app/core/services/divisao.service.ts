import { Injectable } from '@angular/core';
import { Observable, forkJoin, of } from 'rxjs';
import { ApiService } from './api.service';
import { Divisao, DivisaoDTO } from '../models/despesas.model';

@Injectable({ providedIn: 'root' })
export class DivisaoService extends ApiService {
  private readonly PATH = '/divisoes';

  criar(dto: DivisaoDTO): Observable<Divisao> {
    return this.post<Divisao>(`${this.PATH}/criar`, dto);
  }

  listarPorDespesa(despesaId: number): Observable<Divisao[]> {
    return this.get<Divisao[]>(`${this.PATH}/despesa/${despesaId}`);
}

  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }

  /** Cria várias divisões em paralelo (uma por morador) */
  criarVarias(dtos: DivisaoDTO[]): Observable<Divisao[]> {
    if (dtos.length === 0) return of([]);
    return forkJoin(dtos.map((dto) => this.criar(dto)));
  }
}
