import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { CardAnfitriaoService, CardAnfitriaoResponseDTO } from '../core/services/card-anfitriao.service';
import { ApiError } from '../core/services/api.service';

@Component({
  selector: 'app-gerenciar-anuncios',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet, TopNavbarComponent],
  templateUrl: './gerenciar-anuncios.html',
  styleUrl: './gerenciar-anuncios.css',
})
export class GerenciarAnuncios implements OnInit {
  anuncio = signal<CardAnfitriaoResponseDTO | null>(null);
  carregando = signal(true);
  erro = signal<string | null>(null);

  status = computed<'rascunho' | 'ativo'>(() => {
    const card = this.anuncio();
    if (!card) return 'rascunho';
    const completo = !!card.descricao && !!card.localizacao && card.precoMensal != null;
    return completo ? 'ativo' : 'rascunho';
  });

  statusLabel = computed(() => (this.status() === 'ativo' ? 'Ativo' : 'Rascunho'));

  statusBadgeClasses = computed(() =>
    this.status() === 'ativo'
      ? 'bg-secondary-container text-on-secondary-container'
      : 'bg-status-inactive-bg text-status-inactive-text'
  );

  statusDotClasses = computed(() =>
    this.status() === 'ativo' ? 'bg-secondary' : 'bg-slate-400'
  );

  precoFormatado = computed(() => {
    const valor = this.anuncio()?.precoMensal;
    if (valor === null || valor === undefined) return 'A definir';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 0,
    }).format(Number(valor));
  });

  constructor(private cardAnfitriaoService: CardAnfitriaoService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    const anfitriaoId = Number(sessionStorage.getItem('coliv_user_id'));

    if (!anfitriaoId) {
      this.erro.set('Sessão expirada. Faça login novamente.');
      this.carregando.set(false);
      return;
    }

    this.carregando.set(true);
    this.erro.set(null);

    this.cardAnfitriaoService.getCardInfo(anfitriaoId).subscribe({
      next: (card) => {
        this.anuncio.set(card);
        this.carregando.set(false);
      },
      error: (err: ApiError) => {
        if (err.status === 404) {
          this.anuncio.set(null);
          this.erro.set(null);
        } else {
          this.erro.set(err.message ?? 'Não foi possível carregar seu anúncio.');
        }
        this.carregando.set(false);
      },
    });
  }
}