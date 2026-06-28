import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { CommonModule } from '@angular/common';
import { ConviteActionComponent } from './components/convite-action/convite-action';
import { ConviteResponse } from '../core/services/convite.service';
import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, ConviteActionComponent, BottomNavbarComponent],
  templateUrl: './chat.html',
})
export class Chat implements OnInit {

  matchId = 0;
  isAnfitriao  = false;
  anfitriaoId = 0;
  colegaId = 0;
  nomeOutro = '';

  notificacaoConvite: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
  ) {}

  ngOnInit(): void {
    this.matchId = Number(this.route.snapshot.paramMap.get('matchId'));

    const tipo = sessionStorage.getItem('coliv_user_tipo');
    const id   = Number(sessionStorage.getItem('coliv_user_id'));

    this.isAnfitriao = tipo === 'anfitriao';

    if (this.isAnfitriao) {
      this.anfitriaoId = id;
      this.colegaId = Number(sessionStorage.getItem('coliv_chat_outro_id') ?? 0);
    } else {
      this.colegaId = id;
      this.anfitriaoId = Number(sessionStorage.getItem('coliv_chat_outro_id') ?? 0);
    }

    this.nomeOutro = sessionStorage.getItem('coliv_chat_outro_nome') ?? 'Usuário';
  }

  /** Volta para o feed correto conforme o tipo de usuário */
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
      PENDENTE:  'Convite de moradia enviado. Aguardando resposta do colega.',
      ACEITO: 'Convite aceito! Acesso às despesas compartilhadas liberado.',
      RECUSADO: 'O colega recusou o convite de moradia.',
      CANCELADO: 'O convite foi cancelado pelo anfitrião.',
    };

    this.notificacaoConvite = mensagens[convite.status] ?? null;
    setTimeout(() => (this.notificacaoConvite = null), 6000);
  }
}