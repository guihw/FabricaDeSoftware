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
import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

interface ItemChat {
  matchId: number;
  nomeOutro: string;
  outroId: number;
  statusMatch: string;
  convite: ConviteResponse | null;
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
        if (matches.length === 0) return of([] as ItemChat[]);

        const aceitos = matches.filter(m => m.status === 'ACEITO');
        if (aceitos.length === 0) return of([] as ItemChat[]);

        const tasks = aceitos.map(m => {
          const outroId = this.isAnfitriao ? m.colegaId : m.anfitriaoId;
          const nome$ = this.isAnfitriao
            ? this.colegaService.buscarPorId(outroId).pipe(
                map(c => c.nome),
                catchError(() => of('Colega'))
              )
            : this.anfitriaoService.buscarPorId(outroId).pipe(
                map(a => a.nome),
                catchError(() => of('Anfitrião'))
              );

          return forkJoin({
            convite: this.conviteService.buscarPorMatch(m.id).pipe(catchError(() => of(null))),
            nome: nome$,
          }).pipe(
            map(({ convite, nome }): ItemChat => ({
              matchId: m.id,
              nomeOutro: nome,
              outroId,
              statusMatch: m.status,
              convite,
            }))
          );
        });

        return forkJoin(tasks);
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
