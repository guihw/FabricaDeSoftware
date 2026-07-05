import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Colega, ColegaResponse, CreateColegaRequest } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class ColegaService extends ApiService {
  private readonly PATH = '/usuarios/colega';

  // GET /usuarios/colega/listar
  listar(): Observable<Colega[]> {
    return this.get<Colega[]>(`${this.PATH}/listar`);
  }

  // GET /usuarios/colega/buscar/{id}
  buscarPorId(id: number): Observable<Colega> {
    return this.get<Colega>(`${this.PATH}/buscar/${id}`);
  }

  buscarPorIds(ids: number[]): Observable<Colega[]> {
    return this.get<Colega[]>(`${this.PATH}/buscarPorIds?ids=${ids.join(',')}`);
  }

  // POST /usuarios/colega/novo
  criar(request: CreateColegaRequest): Observable<ColegaResponse> {
    return this.post<ColegaResponse>(`${this.PATH}/novo`, request);
  }

  // PUT /usuarios/colega/editar/{id}
  editar(id: number, dados: Partial<Colega>): Observable<Colega> {
    return this.put<Colega>(`${this.PATH}/editar/${id}`, dados);
  }

  // DELETE /usuarios/colega/excluir/{id}
  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }
}
