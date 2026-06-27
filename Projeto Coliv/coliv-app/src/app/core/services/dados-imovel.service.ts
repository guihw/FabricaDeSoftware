import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { DadosImovel, DadosImovelDTO } from '../models/formulario.model';

@Injectable({ providedIn: 'root' })
export class DadosImovelService extends ApiService {
  private readonly PATH = '/formularios/dados-imovel';

  // GET /formularios/dados-imovel/listar
  listar(): Observable<DadosImovel[]> {
    return this.get<DadosImovel[]>(`${this.PATH}/listar`);
  }

  // GET /formularios/dados-imovel/buscar/{id}
  buscarPorId(id: number): Observable<DadosImovel> {
    return this.get<DadosImovel>(`${this.PATH}/buscar/${id}`);
  }

  // GET /formularios/dados-imovel/anfitriao/{anfitriaoId}
  // Retorna 204 (sem corpo) se o anúncio ainda não estiver completo.
  buscarPorAnfitriaoIdSeCompleto(anfitriaoId: number): Observable<DadosImovel> {
    return this.get<DadosImovel>(`${this.PATH}/anfitriao/${anfitriaoId}`);
  }

  // POST /formularios/dados-imovel/{anfitriaoId}/novo-dados-imovel
  criar(anfitriaoId: number, dto: DadosImovelDTO): Observable<DadosImovel> {
    return this.post<DadosImovel>(
      `${this.PATH}/${anfitriaoId}/novo-dados-imovel`,
      dto
    );
  }

  // PUT /formularios/dados-imovel/editar/{anfitriaoId}
  editar(anfitriaoId: number, dto: DadosImovelDTO): Observable<DadosImovel> {
    return this.put<DadosImovel>(`${this.PATH}/editar/${anfitriaoId}`, dto);
  }

  // DELETE /formularios/dados-imovel/excluir/{id}
  excluir(id: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/excluir/${id}`);
  }
}