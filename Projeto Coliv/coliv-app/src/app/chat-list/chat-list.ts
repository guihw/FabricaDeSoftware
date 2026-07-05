import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { AuthService } from '../core/services/auth.service';
import { MatchService, MatchResponse } from '../core/services/match.service';
import { ConviteService, ConviteResponse } from '../core/services/convite.service';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { ColegaService } from '../core/services/colega.service';
import { ArquivoService, ArquivoDTO } from '../core/services/arquivo.service';
import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

interface ItemChat {
  matchId: number;
  nomeOutro: string;
  fotoOutro: string | null;
  outroId: number;
  statusMatch: string;
  convite: ConviteResponse | null;
}

interface PessoaResumo {
  id: number;
  nome: string;
  fotoId: number | null;
}

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule, RouterLink, BottomNavbarComponent, TopNavbarComponent],
  templateUrl: './chat-list.html',
})
export class ChatList implements OnInit {
  isAnfitriao = false;
  usuarioId = 0;

  carregando = signal(true);
  erro = signal<string | null>(null);
  itens = signal<ItemChat[]>([]);

  constructor(
    private auth: AuthService,
    private matchService: MatchService,
    private conviteService: ConviteService,
    private anfitriaoService: AnfitriaoService,
    private colegaService: ColegaService,
    private arquivoService: ArquivoService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.isAnfitriao = this.auth.getUserType() === 'anfitriao';
    this.usuarioId = this.auth.getUserId() ?? 0;
    this.carregar();
  }

  private carregar(): void {
    this.carregando.set(true);
    this.erro.set(null);

    const matches$ = this.isAnfitriao
      ? this.matchService.listarPorAnfitriao(this.usuarioId)
      : this.matchService.listarPorColega(this.usuarioId);

    matches$.pipe(
      switchMap((matches: MatchResponse[]) => {
        const aceitos = matches.filter(m => m.status === 'ACEITO');
        if (aceitos.length === 0) return of([] as ItemChat[]);

        const matchIds = aceitos.map(m => m.id);
        const outroIds = [...new Set(aceitos.map(m => this.isAnfitriao ? m.colegaId : m.anfitriaoId))];

        const convites$ = this.conviteService.buscarPorMatches(matchIds).pipe(
          catchError(() => of([] as ConviteResponse[]))
        );

        const pessoas$ = (this.isAnfitriao
          ? this.colegaService.buscarPorIds(outroIds).pipe(
              map((lista): PessoaResumo[] => lista.map(c => ({ id: c.id, nome: c.nome, fotoId: c.fotoPerfilId ?? null })))
            )
          : this.anfitriaoService.buscarPorIds(outroIds).pipe(
              map((lista): PessoaResumo[] => lista.map(a => ({ id: a.id, nome: a.nome, fotoId: a.fotoPerfil ?? null })))
            )
        ).pipe(catchError(() => of([] as PessoaResumo[])));

        return forkJoin({ convites: convites$, pessoas: pessoas$ }).pipe(
          switchMap(({ convites, pessoas }) => {
            const fotoIds = [...new Set(
              pessoas
                .map(p => p.fotoId)
                .filter((id): id is number => !!id)
            )];

            const fotos$ = fotoIds.length
              ? this.arquivoService.buscarPorIds(fotoIds).pipe(catchError(() => of([] as ArquivoDTO[])))
              : of([] as ArquivoDTO[]);

            return fotos$.pipe(
              map((fotos): ItemChat[] => {
                const conviteMap = new Map(convites.map(c => [c.matchId, c]));
                const pessoaMap = new Map(pessoas.map(p => [p.id, p]));
                const fotoMap = new Map(fotos.map(f => [f.id, f.url]));

                return aceitos.map((m): ItemChat => {
                  const outroId = this.isAnfitriao ? m.colegaId : m.anfitriaoId;
                  const pessoa = pessoaMap.get(outroId);
                  const fotoId = pessoa?.fotoId ?? null;

                  return {
                    matchId: m.id,
                    nomeOutro: pessoa?.nome ?? (this.isAnfitriao ? 'Colega' : 'Anfitrião'),
                    fotoOutro: fotoId ? (fotoMap.get(fotoId) ?? null) : null,
                    outroId,
                    statusMatch: m.status,
                    convite: conviteMap.get(m.id) ?? null,
                  };
                });
              })
            );
          })
        );
      }),
      catchError(() => of([] as ItemChat[]))
    ).subscribe({
      next: (lista) => {
        this.itens.set(lista);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Erro ao carregar conversas.');
        this.carregando.set(false);
      }
    });
  }

  abrirChat(item: ItemChat): void {
    sessionStorage.setItem('coliv_chat_outro_id', String(item.outroId));
    sessionStorage.setItem('coliv_chat_outro_nome', item.nomeOutro);
    if (item.fotoOutro) {
      sessionStorage.setItem('coliv_chat_outro_foto', item.fotoOutro);
    } else {
      sessionStorage.removeItem('coliv_chat_outro_foto');
    }
    this.router.navigate(['/chat', item.matchId]);
  }

  get feedLink(): string {
    return this.isAnfitriao ? '/feedanfitriao' : '/feedcolega';
  }

  labelStatusConvite(convite: ConviteResponse | null): string {
    if (!convite) return 'Sem convite enviado';
    const mapa: Record<string, string> = {
      PENDENTE: 'Aguardando resposta',
      ACEITO: 'Convite aceito',
      RECUSADO: 'Convite recusado',
      CANCELADO: 'Convite cancelado',
    };
    return mapa[convite.status] ?? convite.status;
  }

  corStatusConvite(convite: ConviteResponse | null): string {
    if (!convite) return 'bg-surface-container-high text-on-surface-variant';
    const mapa: Record<string, string> = {
      PENDENTE: 'bg-primary-container text-on-primary-container',
      ACEITO: 'bg-secondary-container text-on-secondary-container',
      RECUSADO: 'bg-error-container text-on-error-container',
      CANCELADO: 'bg-surface-container-high text-on-surface-variant',
    };
    return mapa[convite.status] ?? 'bg-surface-container-high text-on-surface-variant';
  }
}
