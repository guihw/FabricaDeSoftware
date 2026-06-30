import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface ChatResponse {
  id: number;
  anfitriaoId: number;
  colegaId: number;
}

export interface MensagemResponse {
  id: number;
  texto: string;
  tipoUsuario: 'ANFITRIAO' | 'COLEGA';
  criadoEm: string;
  arquivoId: number | null;
  usuarioId: number;
}

@Injectable({ providedIn: 'root' })
export class ChatApiService extends ApiService {

  listarChatsPorUsuario(usuarioId: number, tipo: 'ANFITRIAO' | 'COLEGA'): Observable<ChatResponse[]> {
    return this.get<ChatResponse[]>(`/chat/listar/${usuarioId}/${tipo}`);
  }

  buscarMensagensPorChat(chatId: number): Observable<MensagemResponse[]> {
    return this.get<MensagemResponse[]>(`/chat/mensagem/buscarPorChatId/${chatId}`);
  }
}
