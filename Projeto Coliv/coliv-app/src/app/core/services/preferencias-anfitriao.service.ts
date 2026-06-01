import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  PreferenciasAnfitriao,
  PreferenciasAnfitriaoDTO,
} from '../models/formulario.model';

@Injectable({ providedIn: 'root' })
export class PreferenciasAnfitriaoService extends ApiService {
  private readonly PATH = '/formularios/preferencias-anfitriao';

  // GET /formularios/preferencias-anfitriao/listar
  listar(): Observable<PreferenciasAnfitriao[]> {
    return this.get<PreferenciasAnfitriao[]>(`${this.PATH}/listar`);
  }

  // GET /formularios/preferencias-anfitriao/buscar/{id}
  buscarPorId(id: number): Observable<PreferenciasAnfitriao> {
    return this.get<PreferenciasAnfitriao>(`${this.PATH}/buscar/${id}`);
  }

  // POST /formularios/preferencias-anfitriao/{userId}/nova-preferencia
  criar(
    userId: number,
    dto: PreferenciasAnfitriaoDTO
  ): Observable<PreferenciasAnfitriao> {
    return this.post<PreferenciasAnfitriao>(
      `${this.PATH}/${userId}/nova-preferencia`,
      dto
    );
  }

  // PUT /formularios/preferencias-anfitriao/editar/{anfitriaoId}
  editar(
    anfitriaoId: number,
    dto: PreferenciasAnfitriaoDTO
  ): Observable<PreferenciasAnfitriao> {
    return this.put<PreferenciasAnfitriao>(
      `${this.PATH}/editar/${anfitriaoId}`,
      dto
    );
  }

  // DELETE /formularios/preferencias-anfitriao/excluir/{id}
  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }
}
