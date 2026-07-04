import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ConviteActionComponent } from './components/convite-action/convite-action';
import { ConviteResponse } from '../core/services/convite.service';
import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import { ChatApiService, MensagemResponse } from '../core/services/chat-api.service';
import { WsChatService } from '../core/services/ws-chat.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, ConviteActionComponent, BottomNavbarComponent],
  templateUrl: './chat.html',
})
export class Chat implements OnInit, OnDestroy {

  matchId = 0;
  chatId = 0;
  isAnfitriao = false;
  anfitriaoId = 0;
  colegaId = 0;
  usuarioId = 0;
  nomeOutro = '';
  tipoUsuario: 'ANFITRIAO' | 'COLEGA' = 'COLEGA';

  mensagens: MensagemResponse[] = [];
  textoMensagem = '';
  carregando = true;

  notificacaoConvite: string | null = null;

  @ViewChild('mensagensContainer') mensagensContainer!: ElementRef<HTMLElement>;

  private wsSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private chatApi: ChatApiService,
    private wsChat: WsChatService,
  ) {}

  ngOnInit(): void {
    this.matchId = Number(this.route.snapshot.paramMap.get('matchId'));

    const tipo = sessionStorage.getItem('coliv_user_tipo');
    const id = Number(sessionStorage.getItem('coliv_user_id'));

    this.isAnfitriao = tipo === 'anfitriao';
    this.usuarioId = id;
    this.tipoUsuario = this.isAnfitriao ? 'ANFITRIAO' : 'COLEGA';

    if (this.isAnfitriao) {
      this.anfitriaoId = id;
      this.colegaId = Number(sessionStorage.getItem('coliv_chat_outro_id') ?? 0);
    } else {
      this.colegaId = id;
      this.anfitriaoId = Number(sessionStorage.getItem('coliv_chat_outro_id') ?? 0);
    }

    this.nomeOutro = sessionStorage.getItem('coliv_chat_outro_nome') ?? 'Usuário';

    this.chatApi.listarChatsPorUsuario(this.usuarioId, this.tipoUsuario).subscribe({
      next: (chats) => {
        const chat = chats.find(c =>
          c.anfitriaoId === this.anfitriaoId && c.colegaId === this.colegaId
        );
        if (!chat) { this.carregando = false; return; }
        this.chatId = chat.id;
        this.carregarMensagens();
        this.conectarWs();
      },
      error: () => { this.carregando = false; },
    });
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    this.wsChat.desconectar();
  }

  private carregarMensagens(): void {
    this.chatApi.buscarMensagensPorChat(this.chatId).subscribe({
      next: (msgs) => {
        this.mensagens = msgs;
        this.carregando = false;
        this.scrollParaBaixo();
      },
      error: () => { this.carregando = false; },
    });
  }

  private conectarWs(): void {
    this.wsChat.conectar(this.chatId);
    this.wsSub = this.wsChat.mensagens$.subscribe((evento) => {
      this.mensagens.push(evento.mensagemDTO);
      this.scrollParaBaixo();
    });
  }

  private scrollParaBaixo(): void {
    setTimeout(() => {
      const el = this.mensagensContainer?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    }, 50);
  }

  enviarMensagem(): void {
    const texto = this.textoMensagem.trim();
    if (!texto || !this.chatId) return;
    this.wsChat.enviar(this.usuarioId, this.chatId, texto, this.tipoUsuario);
    this.textoMensagem = '';
  }

  ehMinhaMensagem(msg: MensagemResponse): boolean {
    return msg.usuarioId === this.usuarioId;
  }

  formatarHora(criadoEm: string): string {
    const d = new Date(criadoEm);
    return d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }

  voltar(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate([this.isAnfitriao ? '/feedanfitriao' : '/feedcolega']);
    }
  }

  onConviteAtualizado(convite: ConviteResponse | null): void {
    if (!convite) return;

    const mensagens: Record<string, string> = {
      PENDENTE: 'Convite de moradia enviado. Aguardando resposta do colega.',
      ACEITO: 'Convite aceito! Acesso às despesas compartilhadas liberado.',
      RECUSADO: 'O colega recusou o convite de moradia.',
      CANCELADO: 'O convite foi cancelado pelo anfitrião.',
    };

    this.notificacaoConvite = mensagens[convite.status] ?? null;
    setTimeout(() => (this.notificacaoConvite = null), 6000);
  }
}
