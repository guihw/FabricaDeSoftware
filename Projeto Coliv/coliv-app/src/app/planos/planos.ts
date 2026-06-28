import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { inject } from '@angular/core';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { AuthService } from '../core/services/auth.service';

export interface PlanoRecurso {
  texto: string;
  incluso: boolean;
}

export interface Plano {
  id: 'free' | 'premium' | 'ultra';
  nome: string;
  preco: string;
  periodo: string;
  descricao: string;
  icone: string;
  destaque: boolean;
  badge: string | null;
  corBadge: string;
  recursos: PlanoRecurso[];
  ctaLabel: string;
  ctaRota: string;
}

@Component({
  selector: 'app-planos',
  standalone: true,
  imports: [CommonModule, RouterLink, TopNavbarComponent],
  templateUrl: './planos.html',
  styleUrl: './planos.css',
})
export class Planos {
  private auth = inject(AuthService);
  private router = inject(Router);

  get estaLogado(): boolean {
    return !!this.auth.getUserId();
  }

  get feedLink(): string {
    return this.auth.getUserType() === 'anfitriao' ? '/feedanfitriao' : '/feedcolega';
  }

  planos: Plano[] = [
    {
      id: 'free',
      nome: 'Free',
      preco: 'R$ 0',
      periodo: 'para sempre',
      descricao: 'Ideal para anfitriões que estão começando e querem testar a plataforma.',
      icone: 'home',
      destaque: false,
      badge: null,
      corBadge: '',
      ctaLabel: 'Começar grátis',
      ctaRota: '/cadastro',
      recursos: [
        { texto: '1 anúncio ativo', incluso: true },
        { texto: 'Visibilidade padrão no feed', incluso: true },
        { texto: 'Chat com colegas interessados', incluso: true },
        { texto: 'Verificação de identidade', incluso: true },
        { texto: 'Suporte por e-mail', incluso: true },
        { texto: 'Múltiplos anúncios ativos', incluso: false },
        { texto: 'Destaque no feed', incluso: false },
        { texto: 'Relatórios de visualizações', incluso: false },
        { texto: 'Badge de anfitrião verificado', incluso: false },
        { texto: 'Suporte prioritário', incluso: false },
      ],
    },
    {
      id: 'premium',
      nome: 'Premium',
      preco: 'R$ 49',
      periodo: 'por mês',
      descricao: 'Para anfitriões ativos que gerenciam mais de uma propriedade e querem mais visibilidade.',
      icone: 'workspace_premium',
      destaque: true,
      badge: 'Mais popular',
      corBadge: 'secondary',
      ctaLabel: 'Assinar Premium',
      ctaRota: '/cadastro',
      recursos: [
        { texto: 'Até 5 anúncios ativos', incluso: true },
        { texto: 'Destaque no feed de colegas', incluso: true },
        { texto: 'Chat com colegas interessados', incluso: true },
        { texto: 'Verificação de identidade', incluso: true },
        { texto: 'Suporte prioritário', incluso: true },
        { texto: 'Relatórios de visualizações', incluso: true },
        { texto: 'Badge "Anfitrião Premium"', incluso: true },
        { texto: 'Anúncios ilimitados', incluso: false },
        { texto: 'Suporte dedicado 24/7', incluso: false },
        { texto: 'Consultoria personalizada', incluso: false },
      ],
    },
    {
      id: 'ultra',
      nome: 'Ultra',
      preco: 'R$ 149',
      periodo: 'por mês',
      descricao: 'Para anfitriões de alta renda com portfólio de imóveis e necessidades avançadas.',
      icone: 'diamond',
      destaque: false,
      badge: 'Alta performance',
      corBadge: 'primary',
      ctaLabel: 'Assinar Ultra',
      ctaRota: '/cadastro',
      recursos: [
        { texto: 'Anúncios ilimitados', incluso: true },
        { texto: 'Prioridade máxima no feed', incluso: true },
        { texto: 'Chat com colegas interessados', incluso: true },
        { texto: 'Verificação de identidade', incluso: true },
        { texto: 'Suporte dedicado 24/7', incluso: true },
        { texto: 'Dashboard analytics avançado', incluso: true },
        { texto: 'Badge "Anfitrião Ultra"', incluso: true },
        { texto: 'Gestão de múltiplas propriedades', incluso: true },
        { texto: 'Consultoria personalizada', incluso: true },
        { texto: 'Acesso antecipado a novidades', incluso: true },
      ],
    },
  ];

  irParaPlano(rota: string): void {
    this.router.navigate([rota]);
  }
}
