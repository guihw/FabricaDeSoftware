import { Component, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificacaoService } from '../../../core/services/notificacao.service';
import { Notificacao, TipoNotificacao } from '../../../core/models/notificacao.model';

const ROTAS_POR_TIPO: Record<TipoNotificacao, string> = {
  NOVA_MENSAGEM: '/chats',
  NOVO_MATCH: '/chats',
  DESPESA_ADICIONADA: '/despesas',
  PAGAMENTO_CONFIRMADO: '/despesas',
  NOVA_AVALIACAO: '/perfil',
  CONVITE_RECEBIDO: '/chats',
  CONVITE_ACEITO: '/chats',
};

@Component({
  selector: 'app-notificacao',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notificacao.component.html',
})
export class NotificacaoComponent {

  private notificacaoService = inject(NotificacaoService);
  private router = inject(Router);

  aberto = false;

  get quantidade() { return this.notificacaoService.quantidadeNaoLidas(); }
  get naoLidas() { return this.notificacaoService.naoLidas(); }

  toggleDropdown(): void {
    this.aberto = !this.aberto;
  }

  clicarNotificacao(n: Notificacao): void {
    this.notificacaoService.marcarComoLida(n.id);
    this.aberto = false;
    this.router.navigate([ROTAS_POR_TIPO[n.tipo]]);
  }

  marcarTodasLidas(): void {
    this.notificacaoService.marcarTodasComoLidas();
  }

  formatarTempo(criadoEm: string): string {
    const diff = Date.now() - new Date(criadoEm).getTime();
    const min = Math.floor(diff / 60000);
    if (min < 1) return 'agora';
    if (min < 60) return `${min}m`;
    const h = Math.floor(min / 60);
    if (h < 24) return `${h}h`;
    return `${Math.floor(h / 24)}d`;
  }

  @HostListener('document:click', ['$event'])
  fecharAoClicarFora(event: MouseEvent): void {
    const el = event.target as HTMLElement;
    if (!el.closest('[data-notificacao]')) {
      this.aberto = false;
    }
  }
}
