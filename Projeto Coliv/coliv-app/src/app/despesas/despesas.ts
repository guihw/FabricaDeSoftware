import { Component, inject, OnInit, ViewChild, ElementRef, signal, computed } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';

import { DespesaService } from '../core/services/despesa.service';
import { DivisaoService } from '../core/services/divisao.service';
import { ConviteService } from '../core/services/convite.service';
import { AuthService } from '../core/services/auth.service';
import { ApiError } from '../core/services/api.service';
import { Despesa, DespesaDTO, DivisaoDTO, DespesaView, TipoDespesa, Divisao } from '../core/models/despesas.model';

import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

@Component({
  selector: 'app-despesas',
  imports: [CommonModule, ReactiveFormsModule, BottomNavbarComponent, TopNavbarComponent],
  templateUrl: './despesas.html',
  styleUrl: './despesas.css',
})
export class Despesas implements OnInit {
  private despesaService = inject(DespesaService);
  private divisaoService = inject(DivisaoService);
  private conviteService = inject(ConviteService);
  private authService = inject(AuthService);

  form = new FormGroup({
    nome: new FormControl('', Validators.required),
    valor: new FormControl(0, [Validators.required, Validators.min(1)]),
    dataVencimento: new FormControl(this.hojeISO(), Validators.required),
    categoria: new FormControl(''),
    tipodeDespesa: new FormControl<TipoDespesa>('coletiva', { nonNullable: true, validators: Validators.required }),
    descricao: new FormControl(''),
  });

  isOpen = false;
  modoEdicao = false;
  carregando = signal(true);
  erro = signal<string | null>(null);
  salvando = signal(false);

  usuarioId = 0;
  isAnfitriao = false;

  /** ids de todos os moradores da casa (anfitrião + colegas com convite ACEITO) */
  membrosDaCasa: number[] = [];

  despesas = signal<DespesaView[]>([]);
  despesaSelecionada: DespesaView = this.despesaVazia();

  @ViewChild('dialogRef', { static: false }) dialog!: ElementRef<HTMLDialogElement>;
  @ViewChild('confirmDialog', { static: false }) confirmDialog!: ElementRef<HTMLDialogElement>;

  soma = computed(() =>
    this.despesas().reduce((acc, d) => acc + Number(d.valor), 0)
  );

  totalPago = computed(() =>
    this.despesas().reduce((acc, d) => {
      const minhaDivisao = d.divisoes.find((dv) => dv.usuarioId === this.usuarioId);
      const valorParte = minhaDivisao ? minhaDivisao.valor : 0;
      return d.pago.includes(this.usuarioId) ? acc + valorParte : acc;
    }, 0)
  );

  totalPendente = computed(() => this.soma() - this.totalPago());

  ngOnInit(): void {
    this.usuarioId = this.authService.getUserId() ?? 0;
    this.isAnfitriao = this.authService.getUserType() === 'anfitriao';

    this.carregarMembrosDaCasa()
      .pipe(switchMap(() => this.carregarDespesas()))
      .subscribe();
  }

  // ── Carregamento ──────────────────────────────────────────

  /** Descobre quem são os moradores da casa via convites ACEITOS */
  private carregarMembrosDaCasa() {
    if (this.isAnfitriao) {
      return this.conviteService.listarDoAnfitriao(this.usuarioId).pipe(
        map((convites) => {
          const colegas = convites
            .filter((c) => c.status === 'ACEITO')
            .map((c) => c.colegaId);
          this.membrosDaCasa = [this.usuarioId, ...colegas];
        }),
        catchError(() => {
          this.membrosDaCasa = [this.usuarioId];
          return of(null);
        })
      );
    }

    return this.conviteService.listarParaColega(this.usuarioId).pipe(
      map((convites) => {
        const aceito = convites.find((c) => c.status === 'ACEITO');
        this.membrosDaCasa = aceito
          ? [aceito.anfitriaoId, this.usuarioId]
          : [this.usuarioId];
      }),
      catchError(() => {
        this.membrosDaCasa = [this.usuarioId];
        return of(null);
      })
    );
  }

  private carregarDespesas() {
    this.carregando.set(true);
    this.erro.set(null);

    return this.despesaService.listar().pipe(
      switchMap((despesas) => {
        if (despesas.length === 0) return of([] as DespesaView[]);

        const comDivisoes = despesas.map((despesa) =>
          this.divisaoService.listarPorDespesa(despesa.id).pipe(
            map((divisoes) => this.toView(despesa, divisoes)),
            catchError(() => of(this.toView(despesa, [])))
          )
        );

        return forkJoin(comDivisoes);
      }),
      map((views) => {
        // Mostra só despesas em que o usuário tem uma divisão (faz parte da casa)
        const filtradas = views.filter((v) =>
          v.divisoes.some((dv) => dv.usuarioId === this.usuarioId)
        );
        this.despesas.set(filtradas);
        this.carregando.set(false);
        return filtradas;
      }),
      catchError((err: ApiError) => {
        this.erro.set(err.message ?? 'Erro ao carregar despesas.');
        this.carregando.set(false);
        return of([] as DespesaView[]);
      })
    );
  }

  private toView(despesa: Despesa, divisoes: Divisao[]): DespesaView {
    return {
      id: despesa.id,
      nome: despesa.descricao,
      valor: divisoes.length > 1
        ? divisoes.reduce((acc, d) => acc + d.valor, 0)
        : despesa.valor,
      dataVencimento: despesa.dataVencimento,
      categoria: '',
      tipodeDespesa: divisoes.length > 1 ? 'coletiva' : 'individual',
      divisoes,
      pago: despesa.pago ?? [],
    };
  }

  // ── Modal Nova/Editar ────────────────────────────────────

  openDialog(): void {
    this.dialog.nativeElement.showModal();
  }

  closeDialog(): void {
    this.dialog.nativeElement.close();
    this.form.reset({
      tipodeDespesa: 'coletiva',
      dataVencimento: this.hojeISO(),
      valor: 0,
    });
    this.modoEdicao = false;
  }

  editarDespesa(despesa: DespesaView): void {
    this.modoEdicao = true;
    this.despesaSelecionada = despesa;
    this.openDialog();
    this.form.patchValue({
      nome: despesa.nome,
      valor: despesa.valor,
      dataVencimento: despesa.dataVencimento.substring(0, 10),
      categoria: despesa.categoria,
      tipodeDespesa: despesa.tipodeDespesa,
      descricao: '',
    });
  }

  enviarNovaDespesa(): void {
    if (this.form.invalid || this.salvando()) return;

    this.salvando.set(true);
    const { nome, valor, dataVencimento, tipodeDespesa } = this.form.getRawValue();

    const dto: DespesaDTO = {
      descricao: nome ?? '',
      valor: Number(valor),
      dataVencimento: new Date(dataVencimento ?? this.hojeISO()).toISOString(),
    };

    if (this.modoEdicao) {
      this.despesaService.editar(this.despesaSelecionada.id, dto).subscribe({
        next: () => this.finalizarSalvamento(),
        error: (err: ApiError) => this.erroAoSalvar(err),
      });
      return;
    }

    this.despesaService.criar(dto).subscribe({
      next: (despesaCriada) => this.criarDivisoes(despesaCriada, tipodeDespesa, Number(valor)),
      error: (err: ApiError) => this.erroAoSalvar(err),
    });
  }

  private criarDivisoes(despesa: Despesa, tipo: TipoDespesa, valorTotal: number): void {
    const moradores = tipo === 'coletiva' && this.membrosDaCasa.length > 0
      ? this.membrosDaCasa
      : [this.usuarioId];

    const valorPorPessoa = Number((valorTotal / moradores.length).toFixed(2));

    const dtos: DivisaoDTO[] = moradores.map((usuarioId) => ({
      despesaId: despesa.id,
      usuarioId,
      arquivoId: null,
      valor: valorPorPessoa,
    }));

    this.divisaoService.criarVarias(dtos).subscribe({
      next: () => this.finalizarSalvamento(),
      error: (err: ApiError) => this.erroAoSalvar(err),
    });
  }

  private finalizarSalvamento(): void {
    this.salvando.set(false);
    this.closeDialog();
    this.carregarDespesas().subscribe();
  }

  private erroAoSalvar(err: ApiError): void {
    this.salvando.set(false);
    this.erro.set(err.message ?? 'Erro ao salvar despesa.');
  }

  // ── Painel lateral ───────────────────────────────────────

  openPanel(despesa: DespesaView): void {
    this.isOpen = !this.isOpen;
    this.despesaSelecionada = despesa;
  }

  closePanel(): void {
    this.isOpen = false;
  }

  // ── Pagar / desmarcar ────────────────────────────────────

  euJaPaguei(despesa: DespesaView): boolean {
    return despesa.pago.includes(this.usuarioId);
  }

  minhaParte(despesa: DespesaView): number {
    const divisao = despesa.divisoes.find((d) => d.usuarioId === this.usuarioId);
    return divisao ? divisao.valor : despesa.valor;
  }

  togglePago(despesa: DespesaView): void {
    const acao = this.euJaPaguei(despesa)
      ? this.despesaService.desmarcarComoPago(despesa.id, this.usuarioId)
      : this.despesaService.marcarComoPago(despesa.id, this.usuarioId);

    acao.subscribe({
      next: (atualizada) => {
        despesa.pago = atualizada.pago ?? [];
        this.despesaSelecionada = { ...despesa };
        this.despesas.update((lista) => [...lista]);
      },
      error: (err: ApiError) => this.erro.set(err.message ?? 'Erro ao atualizar pagamento.'),
    });
  }

  // ── Exclusão ──────────────────────────────────────────────

  abrirConfirmDialog(despesa: DespesaView): void {
    this.despesaSelecionada = despesa;
    this.confirmDialog.nativeElement.showModal();
  }

  fecharConfirmDialog(): void {
    this.confirmDialog.nativeElement.close();
  }

  excluirDespesa(): void {
    this.despesaService.excluir(this.despesaSelecionada.id).subscribe({
      next: () => {
        this.fecharConfirmDialog();
        this.closePanel();
        this.carregarDespesas().subscribe();
      },
      error: (err: ApiError) => {
        this.erro.set(err.message ?? 'Erro ao excluir despesa.');
        this.fecharConfirmDialog();
      },
    });
  }

  // ── Helpers ──────────────────────────────────────────────

  private hojeISO(): string {
    return new Date().toISOString().substring(0, 10);
  }

  private despesaVazia(): DespesaView {
    return {
      id: 0, nome: '', valor: 0, dataVencimento: this.hojeISO(),
      categoria: '', tipodeDespesa: 'individual', divisoes: [], pago: [],
    };
  }
}