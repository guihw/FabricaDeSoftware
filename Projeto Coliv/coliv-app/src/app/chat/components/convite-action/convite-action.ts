import {
  Component,
  Input,
  OnInit,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ConviteService, ConviteResponse } from '../../../core/services/convite.service';

@Component({
  selector: 'app-convite-action',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './convite-action.html',
})
export class ConviteActionComponent implements OnInit, OnChanges {
  @Input({ required: true }) matchId!: number;
  @Input({ required: true }) isAnfitriao!: boolean;
  @Input({ required: true }) anfitriaoId!: number;
  @Input({ required: true }) colegaId!: number;
  @Input() mensagemConvite?: string;

  @Output() conviteAtualizado = new EventEmitter<ConviteResponse | null>();

  convite: ConviteResponse | null = null;

  enviando   = false;
  cancelando = false;
  respondendo = false;
  erro: string | null = null;

  constructor(private conviteService: ConviteService) {}

  ngOnInit(): void {
    this.carregarConvite();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['matchId'] && !changes['matchId'].firstChange) {
      this.carregarConvite();
    }
  }

  private carregarConvite(): void {
    if (!this.matchId) return;

    this.conviteService.buscarPorMatch(this.matchId).subscribe({
      next: (convite) => {
        this.convite = convite;
        this.conviteAtualizado.emit(convite);
      },
      error: () => {
        this.convite = null;
      },
    });
  }

  onEnviar(): void {
    if (this.enviando) return;
    this.enviando = true;
    this.erro = null;

    this.conviteService
      .enviar(this.anfitriaoId, {
        matchId: this.matchId,
        colegaId: this.colegaId,
        mensagem: this.mensagemConvite,
      })
      .subscribe({
        next: (convite) => {
          this.convite  = convite;
          this.enviando = false;
          this.conviteAtualizado.emit(convite);
        },
        error: (err) => {
          this.erro     = err.message ?? 'Erro ao enviar convite.';
          this.enviando = false;
        },
      });
  }

  onCancelar(): void {
    if (!this.convite || this.cancelando) return;
    this.cancelando = true;
    this.erro = null;

    this.conviteService.cancelar(this.matchId).subscribe({
      next: (convite) => {
        this.convite    = convite;
        this.cancelando = false;
        this.conviteAtualizado.emit(convite);
      },
      error: (err) => {
        this.erro       = err.message ?? 'Erro ao cancelar convite.';
        this.cancelando = false;
      },
    });
  }

  onAceitar(): void {
    if (!this.convite || this.respondendo) return;
    this.respondendo = true;
    this.erro = null;

    this.conviteService.aceitar(this.matchId).subscribe({
      next: (convite) => {
        this.convite     = convite;
        this.respondendo = false;
        this.conviteAtualizado.emit(convite);
      },
      error: (err) => {
        this.erro        = err.message ?? 'Erro ao aceitar convite.';
        this.respondendo = false;
      },
    });
  }

  onRecusar(): void {
    if (!this.convite || this.respondendo) return;
    this.respondendo = true;
    this.erro = null;

    this.conviteService.recusar(this.matchId).subscribe({
      next: (convite) => {
        this.convite     = convite;
        this.respondendo = false;
        this.conviteAtualizado.emit(convite);
      },
      error: (err) => {
        this.erro        = err.message ?? 'Erro ao recusar convite.';
        this.respondendo = false;
      },
    });
  }
}