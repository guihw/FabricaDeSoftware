import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface VerificacaoAntecedentesRequest {
  usuarioId: number;
  tipoUsuario: 'ANFITRIAO' | 'COLEGA';
}

export interface ResultadoAntecedentesDTO {
  protocolo: string;
  status: string;
  nomeConsultado: string;
  cpfConsultado: string;
  tipoUsuario: string;
  dataVerificacao: string;
  orgaoEmissor: string;
  detalhes: string;
}

@Injectable({ providedIn: 'root' })
export class AntecedentesService extends ApiService {
  verificar(request: VerificacaoAntecedentesRequest): Observable<ResultadoAntecedentesDTO> {
    return this.post<ResultadoAntecedentesDTO>('/antecedentes/verificar', request);
  }
}
