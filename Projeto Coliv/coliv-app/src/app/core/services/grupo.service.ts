import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type TipoUsuarioGrupo = 'ANFITRIAO' | 'COLEGA';

export interface MembroInfo {
  usuarioId: number;
  tipoUsuario: TipoUsuarioGrupo;
}

export interface GrupoResponse {
  grupoId: number;
  membroList: MembroInfo[];
  nomeGrupo: string;
}

@Injectable({ providedIn: 'root' })
export class GrupoService extends ApiService {
  private readonly PATH = '/chat/grupo';

  buscarPorUsuarioId(usuarioId: number, tipoUsuario: TipoUsuarioGrupo): Observable<GrupoResponse> {
    return this.get<GrupoResponse>(`${this.PATH}/buscarPorUsuarioId/${usuarioId}/${tipoUsuario}`);
  }

  removerMembro(grupoId: number, colegaId: number): Observable<void> {
    return this.delete<void>(`${this.PATH}/${grupoId}/membro/${colegaId}`);
  }
}
