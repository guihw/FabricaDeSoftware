import { Component, OnInit, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ColegasCardComponent } from './components/colegas-card-component/colegas-card-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import {
  RecomendacaoService,
  RecomendacaoColegaDTO,
  FeedPageDTO,
} from '../core/services/recomendacao.service';

@Component({
  selector: 'app-feed-anfitriao',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, ColegasCardComponent, TopNavbarComponent],
  templateUrl: './feed-anfitriao.html',
  styleUrl: './feed-anfitriao.css',
})
export class FeedAnfitriao implements OnInit {

  recomendacoes = signal<RecomendacaoColegaDTO[]>([]);
  carregando    = signal(true);
  erro          = signal<string | null>(null);
  pagina        = signal(0);
  temProxima    = signal(false);

  private anfitriaoId: number | null = null;

  constructor(private recomendacaoService: RecomendacaoService) {}

  ngOnInit(): void {
    const id = sessionStorage.getItem('coliv_user_id');
    this.anfitriaoId = id ? Number(id) : null;

    if (!this.anfitriaoId) {
      this.erro.set('Sessão expirada. Faça login novamente.');
      this.carregando.set(false);
      return;
    }

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

  onAceitar(rec: RecomendacaoColegaDTO): void {
    // TODO: implementar lógica de aceite quando o módulo de matches for criado
    console.log('Aceitar colega:', rec.colegaId, rec.nome);
  }

  onRecusar(rec: RecomendacaoColegaDTO): void {
    // Remove localmente do feed até a próxima carga
    this.recomendacoes.update(list => list.filter(r => r.colegaId !== rec.colegaId));
  }
}