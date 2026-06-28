import { Component, EventEmitter, Input, OnDestroy, OnChanges, Output, SimpleChanges, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PagamentoService, PixResponse } from '../../../core/services/pagamento.service';
import { ApiError } from '../../../core/services/api.service';

type EstadoModal = 'carregando' | 'aguardando_pagamento' | 'pago' | 'erro';

@Component({
  selector: 'app-plano-plus-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './plano-plus-modal.html',
  styleUrl: './plano-plus-modal.css',
})
export class PlanoPlusModalComponent implements OnChanges, OnDestroy {

  @Input() aberto = false;
  @Output() fechar = new EventEmitter<void>();
  @Output() pagamentoConfirmado = new EventEmitter<void>();

  estado = signal<EstadoModal>('carregando');
  pix = signal<PixResponse | null>(null);
  erroMsg = signal<string | null>(null);
  copiado = signal(false);

  private pollingSub: Subscription | null = null;
  private readonly INTERVALO_POLLING_MS = 4000;

  constructor(private pagamentoService: PagamentoService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['aberto'] && this.aberto) {
      this.iniciarFluxoPagamento();
    }
    if (changes['aberto'] && !this.aberto) {
      this.pararPolling();
    }
  }

  ngOnDestroy(): void {
    this.pararPolling();
  }

  private iniciarFluxoPagamento(): void {
    this.estado.set('carregando');
    this.erroMsg.set(null);
    this.pix.set(null);
    this.copiado.set(false);

    this.pagamentoService.criarPagamentoPlus().subscribe({
      next: (pix) => {
        this.pix.set(pix);
        this.estado.set('aguardando_pagamento');
        this.iniciarPolling(pix.billingId);
      },
      error: (err: ApiError) => {
        this.erroMsg.set(err.message ?? 'Não foi possível gerar o pagamento. Tente novamente.');
        this.estado.set('erro');
      },
    });
  }

  private iniciarPolling(billingId: string): void {
    this.pararPolling();

    this.pollingSub = interval(this.INTERVALO_POLLING_MS)
      .pipe(switchMap(() => this.pagamentoService.consultarStatus(billingId)))
      .subscribe({
        next: (resposta) => {
          if (resposta.status === 'PAGO') {
            this.estado.set('pago');
            this.pararPolling();
            this.pagamentoConfirmado.emit();
          } else if (resposta.status === 'CANCELADO' || resposta.status === 'EXPIRADO') {
            this.erroMsg.set('O pagamento expirou ou foi cancelado. Gere um novo QR Code.');
            this.estado.set('erro');
            this.pararPolling();
          }
          // se PENDENTE, continua o polling normalmente
        },
        error: () => {
          // falha pontual de rede não derruba o fluxo; tenta de novo no próximo tick
        },
      });
  }

  private pararPolling(): void {
    this.pollingSub?.unsubscribe();
    this.pollingSub = null;
  }

  copiarCodigoPix(): void {
    const pix = this.pix();
    if (!pix) return;

    navigator.clipboard.writeText(pix.pixCopiaCola).then(() => {
      this.copiado.set(true);
      setTimeout(() => this.copiado.set(false), 2500);
    });
  }

  tentarNovamente(): void {
    this.iniciarFluxoPagamento();
  }

  fecharModal(): void {
    this.pararPolling();
    this.fechar.emit();
  }
}
