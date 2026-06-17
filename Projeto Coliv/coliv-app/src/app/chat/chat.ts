import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule }   from '@angular/common';
import { ConviteActionComponent } from './components/convite-action/convite-action';
import { ConviteResponse } from '../core/services/convite.service';


@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, ConviteActionComponent],
  templateUrl: './chat.html'
})
export class Chat implements OnInit {
 
  matchId = 0;
  isAnfitriao = false;
  anfitriaoId = 0;
  colegaId = 0;

  notificacaoConvite: string | null = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // matchId vem da rota /chat/:matchId
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
  }

  onConviteAtualizado(convite: ConviteResponse | null): void {
    if (!convite) return;

    const mensagens: Record<string, string> = {
      PENDENTE: 'Convite de moradia enviado. Aguardando resposta do colega.',
      ACEITO: 'Convite aceito! Acesso às despesas compartilhadas liberado.',
      RECUSADO: 'O colega recusou o convite de moradia.',
      CANCELADO:'O convite foi cancelado pelo anfitrião.',
    };

    this.notificacaoConvite = mensagens[convite.status] ?? null;

    setTimeout(() => (this.notificacaoConvite = null), 6000);
  }
}
