import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import {
  RecomendacaoService,
  RecomendacaoCardAnfitriaoDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';
import { MoradiaCardComponent } from './components/moradia-card-component/moradia-card-component';
import { MoradiaDetailModalComponent } from './components/moradia-detail-modal/moradia-detail-modal';
import { PlanoPlusModalComponent } from './components/plano-plus-modal/plano-plus-modal';
import { ApiError } from '../core/services/api.service';
import { MatchService } from '../core/services/match.service';
import { FotoPerfilService } from '../core/services/foto-perfil.service';

@Component({
  selector: 'app-feed-colega',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    TopNavbarComponent,
    BottomNavbarComponent,
    CommonModule,
    MoradiaCardComponent,
    MoradiaDetailModalComponent,
    PlanoPlusModalComponent,
  ],
  templateUrl: './feed-colega.html',
  styleUrl: './feed-colega.css',
})
export class FeedColega implements OnInit {

  recomendacoes = signal<RecomendacaoCardAnfitriaoDTO[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);
  pagina = signal(0);
  temProxima = signal(false);

  // ── Estado do modal de detalhe ────────────────────────────────
  modalAberto = false;
  recomendacaoSelecionada: RecomendacaoCardAnfitriaoDTO | null = null;

  // ── Estado do modal do Plano Plus ─────────────────────────────
  modalPlanoPlusAberto = false;

  private colegaId: number | null = null;
  likeEmAndamento = signal<Set<number>>(new Set());
  private fotoPerfilService = inject(FotoPerfilService);
  fotoPerfilUrl = this.fotoPerfilService.fotoPerfilUrl;

  constructor(
    private recomendacaoService: RecomendacaoService,
    private matchService: MatchService,
  ) {}

  ngOnInit(): void {
    const id = sessionStorage.getItem('coliv_user_id');
    this.colegaId = id ? Number(id) : null;
    this.fotoPerfilService.hidratar();

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
    if (this.temProxima()) this.carregarPagina(this.pagina() + 1);
  }

  paginaAnterior(): void {
    if (this.pagina() > 0) this.carregarPagina(this.pagina() - 1);
  }

  // ── Modal de detalhe ──────────────────────────────────────────

  abrirDetalhe(rec: RecomendacaoCardAnfitriaoDTO): void {
    this.recomendacaoSelecionada = rec;
    this.modalAberto = true;
  }

  fecharDetalhe(): void {
    this.modalAberto = false;
    // pequeno delay para a animação terminar antes de limpar
    setTimeout(() => (this.recomendacaoSelecionada = null), 250);
  }

  // ── Modal do Plano Plus ───────────────────────────────────────

  abrirModalPlanoPlus(): void {
    this.modalPlanoPlusAberto = true;
  }

  fecharModalPlanoPlus(): void {
    this.modalPlanoPlusAberto = false;
  }

  onPagamentoPlusConfirmado(): void {
    // Plano ativado no backend; aqui você pode atualizar algum estado
    // local se necessário (ex: refletir o selo "Plus" em algum lugar da tela).
  }

  // Set de anfitriãoIds já curtidos (match criado com sucesso)
  curtidos = signal<Set<number>>(new Set());

  // ── Like (pode vir do card ou do modal) ───────────────────────

  onLike(rec: RecomendacaoCardAnfitriaoDTO): void {
    if (!this.colegaId) return;

    const anfitriaoId = rec.card.anfitriaoId;

    // Já curtido: não faz nada
    if (this.curtidos().has(anfitriaoId)) return;
    if (this.likeEmAndamento().has(anfitriaoId)) return;

    this.likeEmAndamento.update(s => new Set([...s, anfitriaoId]));
    this.erro.set(null);

    this.matchService.criar(this.colegaId, anfitriaoId).subscribe({
      next: () => {
        this.likeEmAndamento.update(s => {
          const next = new Set(s); next.delete(anfitriaoId); return next;
        });
        // Marca como curtido — sem redirecionar ao chat
        this.curtidos.update(s => new Set([...s, anfitriaoId]));
      },
      error: (err: ApiError) => {
        this.likeEmAndamento.update(s => {
          const next = new Set(s); next.delete(anfitriaoId); return next;
        });
        this.erro.set(err.message ?? 'Não foi possível registrar o interesse. Tente novamente.');
      },
    });
  }

  corScore(score: number): string {
    if (score >= 80) return 'text-secondary bg-secondary-container';
    if (score >= 60) return 'text-on-primary-container bg-primary-container';
    return 'text-on-surface-variant bg-surface-container-high';
  }
}
