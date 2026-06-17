import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';


export type NivelDeSociabilidade = 'INTROVERTIDO' | 'MODERADO' | 'SOCIAVEL';
export type NivelDeLimpeza = 'BAIXO' | 'MEDIO' | 'ALTO';
export type HabitoDeTrabalho = 'HOME_OFFICE' | 'HIBRIDO' | 'PRESENCIAL';

// Espelha: PreferenciasColegaDTO.java
export interface PreferenciasColegaDTO {
  precoMinimo: number;
  precoMaximo: number;
  localizacao: string;
  horarioDeSono: string;         
  nivelDeSociabilidade: NivelDeSociabilidade;
  nivelDeLimpeza: NivelDeLimpeza;
  habitoDeTrabalho: HabitoDeTrabalho;
  aceitaAnimais: boolean;
}

// Espelha: PreferenciasColegaResponse.java
export interface PreferenciasColegaResponse extends PreferenciasColegaDTO {
  id: number;
}

@Injectable({ providedIn: 'root' })
export class PreferenciasColegaService extends ApiService {
  private readonly PATH = '/formularios/preferencias-colega';

  // GET /formularios/preferencias-colega/listar
  listar(): Observable<PreferenciasColegaResponse[]> {
    return this.get<PreferenciasColegaResponse[]>(`${this.PATH}/listar`);
  }

  // GET /formularios/preferencias-colega/buscar/{id}
  buscarPorId(id: number): Observable<PreferenciasColegaResponse> {
    return this.get<PreferenciasColegaResponse>(`${this.PATH}/buscar/${id}`);
  }

  // POST /formularios/preferencias-colega/{colegaId}/nova-preferencia
  criar(colegaId: number, dto: PreferenciasColegaDTO): Observable<PreferenciasColegaResponse> {
    return this.post<PreferenciasColegaResponse>(
      `${this.PATH}/${colegaId}/nova-preferencia`,
      dto
    );
  }

  // PUT /formularios/preferencias-colega/editar/{id}
  editar(id: number, dto: PreferenciasColegaDTO): Observable<PreferenciasColegaResponse> {
    return this.put<PreferenciasColegaResponse>(`${this.PATH}/editar/${id}`, dto);
  }

  // DELETE /formularios/preferencias-colega/excluir/{id}
  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }
}