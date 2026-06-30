import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import {
  RecomendacaoService,
  RecomendacaoColegaDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';
import { ColegasCardComponent } from './components/colegas-card-component/colegas-card-component';
import { ColegaDetailModalComponent } from './components/colega-detail-modal/colega-detail-modal';
import { ApiError } from '../core/services/api.service';
import { MatchService } from '../core/services/match.service';
import { CardAnfitriaoService, CardAnfitriaoResponseDTO } from '../core/services/card-anfitriao.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-feed-anfitriao',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RouterOutlet,
    TopNavbarComponent,
    ColegasCardComponent,
    ColegaDetailModalComponent, 
  ],
  templateUrl: './feed-anfitriao.html',
  styleUrl: './feed-anfitriao.css',
})
export class FeedAnfitriao implements OnInit {

  recomendacoes = signal<RecomendacaoColegaDTO[]>([]);
  imovelCard = signal<CardAnfitriaoResponseDTO | null>(null);
  carregando  = signal(true);
  erro = signal<string | null>(null);
  pagina = signal(0);
  temProxima = signal(false);
  acaoEmAndamento  = signal<Set<number>>(new Set());
  fotoPerfilUrl = signal<string | null>(null);

  // ── Estado do modal ───────────────────────────────────────────
  modalAberto              = false;
  recomendacaoSelecionada: RecomendacaoColegaDTO | null = null;

  private anfitriaoId: number | null = null;

  constructor(
    private recomendacaoService: RecomendacaoService,
    private matchService: MatchService,
    private cardAnfitriaoService: CardAnfitriaoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const id = sessionStorage.getItem('coliv_user_id');
    this.anfitriaoId = id ? Number(id) : null;
    this.fotoPerfilUrl.set(sessionStorage.getItem('coliv_foto_perfil'));

    if (!this.anfitriaoId) {
      this.erro.set('Sessão expirada. Faça login novamente.');
      this.carregando.set(false);
      return;
    }

    this.cardAnfitriaoService.getCardInfo(this.anfitriaoId).pipe(
      catchError(() => of(null))
    ).subscribe(card => this.imovelCard.set(card));

    this.carregarPagina(0);
  }

  carregarPagina(pagina: number): void {
    if (!this.anfitriaoId) return;

    this.carregando.set(true);
    this.erro.set(null);

    this.recomendacaoService.feedAnfitriao(this.anfitriaoId, pagina).subscribe({
      next: (feed: FeedPageDTO<RecomendacaoColegaDTO>) => {
        this.recomendacoes.set(feed.itens);
        this.pagina.set(feed.pagina);
        this.temProxima.set(feed.temProxima);
        this.carregando.set(false);
      },
      error: (err: ApiError) => {
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

  // ── Modal ─────────────────────────────────────────────────────

  abrirDetalhe(rec: RecomendacaoColegaDTO): void {
    this.recomendacaoSelecionada = rec;
    this.modalAberto = true;
  }

  fecharDetalhe(): void {
    this.modalAberto = false;
    setTimeout(() => (this.recomendacaoSelecionada = null), 250);
  }

  // ── Ações (podem vir do card ou do modal) ─────────────────────

  onAceitar(rec: RecomendacaoColegaDTO): void {
    if (!this.anfitriaoId) return;

    const colegaId = rec.colegaId;
    if (this.acaoEmAndamento().has(colegaId)) return;

    this.acaoEmAndamento.update(s => new Set([...s, colegaId]));
    this.erro.set(null);

    this.matchService.criarAceito(colegaId, this.anfitriaoId).subscribe({
      next: (match) => {
        this.acaoEmAndamento.update(s => { const n = new Set(s); n.delete(colegaId); return n; });
        this.recomendacoes.update(lista => lista.filter(r => r.colegaId !== colegaId));
        sessionStorage.setItem('coliv_chat_outro_id',   String(colegaId));
        sessionStorage.setItem('coliv_chat_outro_nome', rec.nome);
        this.router.navigate(['/chat', match.id]);
      },
      error: (err: ApiError) => {
        this.acaoEmAndamento.update(s => { const n = new Set(s); n.delete(colegaId); return n; });
        this.erro.set(err.message ?? 'Não foi possível criar o match. Tente novamente.');
      },
    });
  }

  onRecusar(rec: RecomendacaoColegaDTO): void {
    this.recomendacoes.update(lista => lista.filter(r => r.colegaId !== rec.colegaId));
  }
}