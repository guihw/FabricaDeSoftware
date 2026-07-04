import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { DespesaDTO, Despesa} from '../models/despesas.model';

@Injectable({ providedIn: 'root' })
export class DespesaService extends ApiService {
  private readonly PATH = '/despesas';

  criar(dto: DespesaDTO): Observable<Despesa> {
    return this.post<Despesa>(`${this.PATH}/criar`, dto);
  }

  listar(): Observable<Despesa[]> {
    return this.get<Despesa[]>(`${this.PATH}/listar`);
  }

  buscarPorId(id: number): Observable<Despesa> {
    return this.get<Despesa>(`${this.PATH}/buscar/${id}`);
  }

  editar(id: number, dto: DespesaDTO): Observable<Despesa> {
    return this.put<Despesa>(`${this.PATH}/editar/${id}`, dto);
  }

  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }

  marcarComoPago(id: number, usuarioId: number): Observable<Despesa> {
    return this.patch<Despesa>(`${this.PATH}/${id}/pagar/${usuarioId}`);
  }

  desmarcarComoPago(id: number, usuarioId: number): Observable<Despesa> {
    return this.patch<Despesa>(`${this.PATH}/${id}/desmarcar/${usuarioId}`);
  }

  protected override patch<T>(path: string, body?: unknown): Observable<T> {
    return this.http
      .patch<T>(`${this.baseUrl}${path}`, body ?? {});
  }
}