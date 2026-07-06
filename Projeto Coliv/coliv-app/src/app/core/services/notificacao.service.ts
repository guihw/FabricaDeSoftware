import { Injectable, NgZone, OnDestroy, signal, computed } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { Notificacao } from '../models/notificacao.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificacaoService extends ApiService implements OnDestroy {

  private notificacoes = signal<Notificacao[]>([]);
  readonly naoLidas = computed(() => this.notificacoes().filter(n => !n.lida));
  readonly quantidadeNaoLidas = computed(() => this.naoLidas().length);

  private _toastMatch = signal<Notificacao | null>(null);
  readonly toastMatch = this._toastMatch.asReadonly();
  private filaToastMatch: Notificacao[] = [];
  private toastTimeout?: ReturnType<typeof setTimeout>;

  private client: Client | null = null;
  private subscription: StompSubscription | null = null;

  constructor(private ngZone: NgZone, private auth: AuthService) {
    super();
  }

  inicializar(): void {
    if (this.client) return;

    const userId = this.auth.getUserId();
    if (!userId) return;

    this.get<Notificacao[]>('/notificacoes').subscribe({
      next: (lista) => this.notificacoes.set(lista),
    });

    this.conectarWs(userId);
  }

  private conectarWs(userId: number): void {
    this.client = new Client({
      webSocketFactory: () => new WebSocket(`${environment.wsUrl}/ws-connect/websocket`),
      reconnectDelay: 5000,
      onConnect: () => {
        this.subscription = this.client!.subscribe(
          `/topic/notificacoes.${userId}`,
          (frame) => {
            this.ngZone.run(() => {
              const nova: Notificacao = JSON.parse(frame.body);
              let adicionada = false;
              this.notificacoes.update(lista => {
                const jaExiste = lista.some(n => n.id === nova.id);
                if (jaExiste) return lista;
                adicionada = true;
                return [nova, ...lista];
              });
              if (adicionada && nova.tipo === 'NOVO_MATCH') {
                this.mostrarToastMatch(nova);
              }
            });
          }
        );
      },
    });

    this.client.activate();
  }

  marcarComoLida(id: number): void {
    this.patch(`/notificacoes/${id}/lida`, {}).subscribe({
      next: () => {
        this.notificacoes.update(lista =>
          lista.map(n => n.id === id ? { ...n, lida: true } : n)
        );
      },
    });
  }

  marcarTodasComoLidas(): void {
    this.naoLidas().forEach(n => this.marcarComoLida(n.id));
  }

  private mostrarToastMatch(notificacao: Notificacao): void {
    this.filaToastMatch.push(notificacao);
    if (this._toastMatch() === null) {
      this.exibirProximoToastMatch();
    }
  }

  private exibirProximoToastMatch(): void {
    clearTimeout(this.toastTimeout);
    const proximo = this.filaToastMatch.shift() ?? null;
    this._toastMatch.set(proximo);
    if (proximo) {
      this.toastTimeout = setTimeout(() => this.exibirProximoToastMatch(), 8000);
    }
  }

  fecharToastMatch(): void {
    this.exibirProximoToastMatch();
  }

  desconectar(): void {
    this.subscription?.unsubscribe();
    this.subscription = null;
    this.client?.deactivate();
    this.client = null;
    this.notificacoes.set([]);
    this.filaToastMatch = [];
    this.fecharToastMatch();
  }

  ngOnDestroy(): void {
    this.desconectar();
  }
}
