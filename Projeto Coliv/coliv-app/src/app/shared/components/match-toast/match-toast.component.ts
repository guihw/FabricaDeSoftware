import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificacaoService } from '../../../core/services/notificacao.service';

@Component({
  selector: 'app-match-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './match-toast.component.html',
})
export class MatchToastComponent {
  private notificacaoService = inject(NotificacaoService);
  private router = inject(Router);

  get notificacao() {
    return this.notificacaoService.toastMatch();
  }

  abrirConversa(): void {
    const n = this.notificacao;
    if (!n) return;
    this.notificacaoService.marcarComoLida(n.id);
    this.notificacaoService.fecharToastMatch();
    this.router.navigate(['/chats']);
  }

  fechar(event: MouseEvent): void {
    event.stopPropagation();
    this.notificacaoService.fecharToastMatch();
  }
}
