import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import {
  RecomendacaoService,
  RecomendacaoCardAnfitriaoDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';
import { MoradiaCardComponent } from './components/moradia-card-component/moradia-card-component';
import { ApiError } from '../core/services/api.service';
import { MatchService } from '../core/services/match.service';

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

  likeEmAndamento = signal<Set<number>>(new Set());

  constructor(private recomendacaoService: RecomendacaoService,
    private matchService: MatchService,
    private router: Router,) {}

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

  onLike(rec: RecomendacaoCardAnfitriaoDTO): void {
    if (!this.colegaId) return;
 
    const anfitriaoId = rec.card.anfitriaoId;
 
    // Evita múltiplos cliques enquanto a requisição está em andamento
    if (this.likeEmAndamento().has(anfitriaoId)) return;
    this.likeEmAndamento.update(s => new Set([...s, anfitriaoId]));
    this.erro.set(null);
 
    this.matchService.criar(this.colegaId, anfitriaoId).subscribe({
      next: (match) => {
        this.likeEmAndamento.update(s => {
          const next = new Set(s);
          next.delete(anfitriaoId);
          return next;
        });
       
        sessionStorage.setItem('coliv_chat_outro_id', String(anfitriaoId));
        this.router.navigate(['/chat', match.id]);
      },
      error: (err: ApiError) => {
        this.likeEmAndamento.update(s => {
          const next = new Set(s);
          next.delete(anfitriaoId);
          return next;
        });
        this.erro.set(err.message ?? 'Não foi possível criar o match. Tente novamente.');
      },
    });
  }
 
  corScore(score: number): string {
    if (score >= 80) return 'text-secondary bg-secondary-container';
    if (score >= 60) return 'text-on-primary-container bg-primary-container';
    return 'text-on-surface-variant bg-surface-container-high';
  }
}