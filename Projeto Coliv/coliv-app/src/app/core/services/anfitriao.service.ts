import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Anfitriao, AnfitriaoDTO } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AnfitriaoService extends ApiService {
  private readonly PATH = '/usuarios/anfitriao';

  // GET /usuarios/anfitriao/listar
  listar(): Observable<Anfitriao[]> {
    return this.get<Anfitriao[]>(`${this.PATH}/listar`);
  }

  // GET /usuarios/anfitriao/buscar/{id}
  buscarPorId(id: number): Observable<Anfitriao> {
    return this.get<Anfitriao>(`${this.PATH}/buscar/${id}`);
  }

  buscarPorIds(ids: number[]): Observable<Anfitriao[]> {
    return this.get<Anfitriao[]>(`${this.PATH}/buscarPorIds?ids=${ids.join(',')}`);
  }

  // POST /usuarios/anfitriao/novo
  criar(dto: AnfitriaoDTO): Observable<Anfitriao> {
    return this.post<Anfitriao>(`${this.PATH}/novo`, dto);
  }

  // PUT /usuarios/anfitriao/editar/{id}
  editar(id: number, dados: Partial<Anfitriao>): Observable<Anfitriao> {
    return this.put<Anfitriao>(`${this.PATH}/editar/${id}`, dados);
  }

  // DELETE /usuarios/anfitriao/excluir/{id}
  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }
}
