import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface PixResponse {
  billingId: string;
  qrCodeBase64: string;
  pixCopiaCola: string;
  status: 'PENDENTE' | 'PAGO' | 'CANCELADO' | 'EXPIRADO';
}

export interface StatusPagamentoResponse {
  billingId: string;
  status: 'PENDENTE' | 'PAGO' | 'CANCELADO' | 'EXPIRADO';
}

@Injectable({ providedIn: 'root' })
export class PagamentoService extends ApiService {

  criarPagamentoPlus(): Observable<PixResponse> {
    return this.post<PixResponse>('/pagamentos/plus', {});
  }

  consultarStatus(billingId: string): Observable<StatusPagamentoResponse> {
    return this.get<StatusPagamentoResponse>(`/pagamentos/${billingId}/status`);
  }
}
