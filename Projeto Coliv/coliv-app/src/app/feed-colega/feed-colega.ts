import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import {
  RecomendacaoService,
  RecomendacaoCardAnfitriaoDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';
import { MoradiaCardComponent } from './components/moradia-card-component/moradia-card-component';

@Component({
  selector: 'app-feed-colega',
  standalone: true,
  imports: [RouterOutlet, TopNavbarComponent, CommonModule, MoradiaCardComponent],
  templateUrl: './feed-colega.html',
  styleUrl: './feed-colega.css',
})
export class FeedColega implements OnInit {

  recomendacoes = signal<RecomendacaoCardAnfitriaoDTO[]>([]);
  carregando    = signal(true);
  erro          = signal<string | null>(null);
  pagina        = signal(0);
  temProxima    = signal(false);

  private colegaId: number | null = null;

  constructor(private recomendacaoService: RecomendacaoService) {}

  ngOnInit(): void {
    const id = sessionStorage.getItem('coliv_user_id');
    this.colegaId = id ? Number(id) : null;

    if (!this.colegaId) {
      this.erro.set('Sessão expirada. Faça login novamente.');
      this.carregando.set(false);
      return;
    }

    this.carregarPagina(0);
  }

  carregarPagina(pagina: number): void {
    if (!this.colegaId) return;

    this.carregando.set(true);
    this.erro.set(null);

    this.recomendacaoService.feedColega(this.colegaId, pagina).subscribe({
      next: (feed: FeedPageDTO<RecomendacaoCardAnfitriaoDTO>) => {
        this.recomendacoes.set(feed.itens);
        this.pagina.set(feed.pagina);
        this.temProxima.set(feed.temProxima);
        this.carregando.set(false);
      },
      error: (err) => {
        this.erro.set(err.message);
        this.carregando.set(false);
      },
    });
  }

  proximaPagina(): void {
    if (this.temProxima()) {
      this.carregarPagina(this.pagina() + 1);
    }
  }

  paginaAnterior(): void {
    if (this.pagina() > 0) {
      this.carregarPagina(this.pagina() - 1);
    }
  }

  /** Retorna label de cor de acordo com o score de compatibilidade */
  corScore(score: number): string {
    if (score >= 80) return 'text-secondary bg-secondary-container';
    if (score >= 60) return 'text-on-primary-container bg-primary-container';
    return 'text-on-surface-variant bg-surface-container-high';
  }
}