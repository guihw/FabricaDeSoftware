import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MensagemResponse } from './chat-api.service';

export interface WsMensagemEvento {
  contexto: string;
  mensagemDTO: MensagemResponse;
}

@Injectable({ providedIn: 'root' })
export class WsChatService implements OnDestroy {

  private client: Client | null = null;
  private subscription: StompSubscription | null = null;
  private mensagem$ = new Subject<WsMensagemEvento>();

  readonly mensagens$ = this.mensagem$.asObservable();

  constructor(private ngZone: NgZone) {}

  conectar(chatId: number): void {
    this.desconectar();

    this.client = new Client({
      webSocketFactory: () => new WebSocket(`${environment.wsUrl}/ws-connect/websocket`),
      reconnectDelay: 5000,
      onConnect: () => {
        this.subscription = this.client!.subscribe(
          `/topic/chat/${chatId}`,
          (frame) => {
            this.ngZone.run(() => {
              const evento: WsMensagemEvento = JSON.parse(frame.body);
              this.mensagem$.next(evento);
            });
          }
        );
      },
    });

    this.client.activate();
  }

  enviar(usuarioId: number, chatId: number, texto: string, tipoUsuario: 'ANFITRIAO' | 'COLEGA'): void {
    if (!this.client?.connected) return;

    this.client.publish({
      destination: `/app/chat/mensagem/nova/${usuarioId}/${chatId}`,
      body: JSON.stringify({ texto, tipoUsuario, arquivoId: null }),
    });
  }

  desconectar(): void {
    this.subscription?.unsubscribe();
    this.subscription = null;
    this.client?.deactivate();
    this.client = null;
  }

  ngOnDestroy(): void {
    this.desconectar();
  }
}
