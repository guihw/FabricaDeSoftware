import { Component, inject, OnInit, ViewChild, ElementRef, signal, computed } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of, Observable } from 'rxjs';
import { switchMap, catchError, map } from 'rxjs/operators';

import { DespesaService } from '../core/services/despesa.service';
import { DivisaoService } from '../core/services/divisao.service';
import { GrupoService, MembroInfo, TipoUsuarioGrupo } from '../core/services/grupo.service';
import { AuthService } from '../core/services/auth.service';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { ColegaService } from '../core/services/colega.service';
import { ApiError } from '../core/services/api.service';
import { Despesa, DespesaDTO, DivisaoDTO, DespesaView, TipoDespesa, Divisao } from '../core/models/despesas.model';

import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import { TopNavbarComponent }    from '../shared/components/top-navbar-component/top-navbar-component';

@Component({
  selector: 'app-despesas',
  imports: [CommonModule, ReactiveFormsModule, BottomNavbarComponent, TopNavbarComponent, RouterLink],
  templateUrl: './despesas.html',
  styleUrl: './despesas.css',
})
export class Despesas implements OnInit {
  private despesaService   = inject(DespesaService);
  private divisaoService   = inject(DivisaoService);
  private grupoService     = inject(GrupoService);
  private authService      = inject(AuthService);
  private anfitriaoService = inject(AnfitriaoService);
  private colegaService    = inject(ColegaService);

  form = new FormGroup({
    nome:           new FormControl('', Validators.required),
    valor:          new FormControl(0, [Validators.required, Validators.min(1)]),
    dataVencimento: new FormControl(this.hojeISO(), Validators.required),
    categoria:      new FormControl(''),
    tipodeDespesa:  new FormControl<TipoDespesa>('coletiva', {
      nonNullable: true,
      validators: Validators.required,
    }),
    descricao: new FormControl(''),
  });

  isOpen      = false;
  modoEdicao  = false;
  carregando  = signal(true);
  erro        = signal<string | null>(null);
  salvando    = signal(false);

  usuarioId    = 0;
  anfitriaoId  = 0;
  grupoId      = 0;
  isAnfitriao  = false;
  membrosDaCasa: number[] = [];
  membrosInfo: { id: number; label: string }[] = [];
  temColegaAceito = signal(false);
  temConviteAceito = signal(false);

  // Divisão personalizada
  divisaoPersonalizada = false;
  valoresCustomizados: Record<number, number> = {};

  despesas         = signal<DespesaView[]>([]);
  despesaSelecionada: DespesaView = this.despesaVazia();

  @ViewChild('dialogRef',   { static: false }) dialog!: ElementRef<HTMLDialogElement>;
  @ViewChild('confirmDialog', { static: false }) confirmDialog!: ElementRef<HTMLDialogElement>;

  soma = computed(() =>
    this.despesas().reduce((acc, d) => acc + Number(d.valor), 0)
  );

  totalPago = computed(() =>
    this.despesas().reduce((acc, d) => {
      const minhaDivisao = d.divisoes.find(dv => dv.usuarioId === this.usuarioId);
      const valorParte   = minhaDivisao ? minhaDivisao.valor : 0;
      return d.pago.includes(this.usuarioId) ? acc + valorParte : acc;
    }, 0)
  );

  totalPendente = computed(() =>
    this.despesas().reduce((acc, d) => {
      if (d.pago.includes(this.usuarioId)) return acc;
      const minhaDivisao = d.divisoes.find(dv => dv.usuarioId === this.usuarioId);
      return acc + (minhaDivisao ? minhaDivisao.valor : 0);
    }, 0)
  );

  ngOnInit(): void {
    this.usuarioId   = this.authService.getUserId() ?? 0;
    this.isAnfitriao = this.authService.getUserType() === 'anfitriao';

    this.carregarMembrosDaCasa()
      .pipe(switchMap(() => this.carregarDespesas()))
      .subscribe();
  }

  // ── Carregamento ──────────────────────────────────────────────

  private carregarMembrosDaCasa() {
    const tipoUsuario: TipoUsuarioGrupo = this.isAnfitriao ? 'ANFITRIAO' : 'COLEGA';

    return this.grupoService.buscarPorUsuarioId(this.usuarioId, tipoUsuario).pipe(
      switchMap(grupo => {
        const anfitriaoMembro = grupo.membroList.find(m => m.tipoUsuario === 'ANFITRIAO');
        this.anfitriaoId = anfitriaoMembro?.usuarioId ?? this.usuarioId;
        this.grupoId = grupo.grupoId;
        this.membrosDaCasa = grupo.membroList.map(m => m.usuarioId);
        this.temColegaAceito.set(grupo.membroList.some(m => m.tipoUsuario === 'COLEGA'));
        this.temConviteAceito.set(true);

        return forkJoin(
          grupo.membroList.map(membro =>
            membro.usuarioId === this.usuarioId
              ? of({ id: membro.usuarioId, label: 'Você' })
              : this.resolverLabelMembro(membro)
          )
        ).pipe(map(membrosInfo => { this.membrosInfo = membrosInfo; return null; }));
      }),
      catchError(() => {
        this.anfitriaoId = this.isAnfitriao ? this.usuarioId : 0;
        this.grupoId = 0;
        this.membrosDaCasa = [this.usuarioId];
        this.membrosInfo = [{ id: this.usuarioId, label: 'Você' }];
        this.temColegaAceito.set(false);
        this.temConviteAceito.set(false);
        return of(null);
      })
    );
  }

  private resolverLabelMembro(membro: MembroInfo): Observable<{ id: number; label: string }> {
    const rotuloPadrao = membro.tipoUsuario === 'ANFITRIAO' ? 'Anfitrião' : 'Colega';

    if (membro.tipoUsuario === 'ANFITRIAO') {
      return this.anfitriaoService.buscarPorId(membro.usuarioId).pipe(
        map(usuario => ({ id: membro.usuarioId, label: usuario?.nome ?? rotuloPadrao })),
        catchError(() => of({ id: membro.usuarioId, label: rotuloPadrao }))
      );
    }

    return this.colegaService.buscarPorId(membro.usuarioId).pipe(
      map(usuario => ({ id: membro.usuarioId, label: usuario?.nome ?? rotuloPadrao })),
      catchError(() => of({ id: membro.usuarioId, label: rotuloPadrao }))
    );
  }

  podeRemoverMembro(membro: { id: number; label: string }): boolean {
    if (membro.id === this.anfitriaoId) return false;
    return this.isAnfitriao || membro.id === this.usuarioId;
  }

  removerMembro(membro: { id: number; label: string }): void {
    const mensagem = membro.id === this.usuarioId
      ? 'Tem certeza que deseja sair desta casa?'
      : `Tem certeza que deseja remover ${membro.label} da casa?`;

    if (!confirm(mensagem)) return;

    this.grupoService.removerMembro(this.grupoId, membro.id).subscribe({
      next: () => {
        this.carregarMembrosDaCasa()
          .pipe(switchMap(() => this.carregarDespesas()))
          .subscribe();
      },
      error: (err: ApiError) => this.erro.set(err.message ?? 'Erro ao remover morador.'),
    });
  }

  private carregarDespesas() {
    this.carregando.set(true);
    this.erro.set(null);

    if (!this.anfitriaoId) {
      this.despesas.set([]);
      this.carregando.set(false);
      return of([] as DespesaView[]);
    }

    return this.despesaService.listarPorAnfitriao(this.anfitriaoId).pipe(
      switchMap(despesas => {
        if (despesas.length === 0) return of([] as DespesaView[]);

        const comDivisoes = despesas.map(despesa =>
          this.divisaoService.listarPorDespesa(despesa.id).pipe(
            map(divisoes => this.toView(despesa, divisoes)),
            catchError(() => of(this.toView(despesa, [])))
          )
        );

        return forkJoin(comDivisoes);
      }),
      map(views => {
        const filtradas = views.filter(v =>
          v.divisoes.some(dv => dv.usuarioId === this.usuarioId)
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
      valor: divisoes.length > 1 ? divisoes.reduce((acc, d) => acc + d.valor, 0) : despesa.valor,
      dataVencimento: despesa.dataVencimento,
      categoria:      '',
      tipodeDespesa:  divisoes.length > 1 ? 'coletiva' : 'individual',
      divisoes,
      pago: despesa.pago ?? [],
    };
  }

  // ── Modal Nova/Editar ─────────────────────────────────────────

  openDialog(): void  { this.dialog.nativeElement.showModal(); }

  closeDialog(): void {
    this.dialog.nativeElement.close();
    this.form.reset({ tipodeDespesa: 'coletiva', dataVencimento: this.hojeISO(), valor: 0 });
    this.modoEdicao = false;
    this.divisaoPersonalizada = false;
    this.valoresCustomizados = {};
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
    this.divisaoPersonalizada = false;
    this.valoresCustomizados = {};
  }

  enviarNovaDespesa(): void {
    if (this.form.invalid || this.salvando()) return;
    if (!this.divisoesValidas()) return;

    this.salvando.set(true);
    const { nome, valor, dataVencimento, tipodeDespesa } = this.form.getRawValue();

    const dto: DespesaDTO = {
      descricao: nome ?? '',
      valor: Number(valor),
      dataVencimento: new Date(dataVencimento ?? this.hojeISO()).toISOString(),
      anfitriaoId: this.anfitriaoId,
    };

    if (this.modoEdicao) {
      this.despesaService.editar(this.despesaSelecionada.id, dto).subscribe({
        next:  () => this.finalizarSalvamento(),
        error: (err: ApiError) => this.erroAoSalvar(err),
      });
      return;
    }

    this.despesaService.criar(dto).subscribe({
      next:  despesaCriada => this.criarDivisoes(despesaCriada, tipodeDespesa, Number(valor)),
      error: (err: ApiError) => this.erroAoSalvar(err),
    });
  }

  private criarDivisoes(despesa: Despesa, tipo: TipoDespesa, valorTotal: number): void {
    const moradores = tipo === 'coletiva' && this.membrosDaCasa.length > 0
      ? this.membrosDaCasa
      : [this.usuarioId];

    let dtos: DivisaoDTO[];

    if (tipo === 'coletiva' && this.divisaoPersonalizada) {
      dtos = moradores.map(usuarioId => ({
        despesaId: despesa.id,
        usuarioId,
        arquivoId: null,
        valor: this.valoresCustomizados[usuarioId] ?? 0,
      }));
    } else {
      const valorPorPessoa = Number((valorTotal / moradores.length).toFixed(2));
      dtos = moradores.map(usuarioId => ({
        despesaId: despesa.id,
        usuarioId,
        arquivoId: null,
        valor:     valorPorPessoa,
      }));
    }

    this.divisaoService.criarVarias(dtos).subscribe({
      next:  () => this.finalizarSalvamento(),
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

  // ── Divisão personalizada ─────────────────────────────────────

  ativarDivisaoPersonalizada(): void {
    this.divisaoPersonalizada = true;
    const valorTotal = Number(this.form.get('valor')?.value ?? 0);
    const perPessoa = Number((valorTotal / this.membrosDaCasa.length).toFixed(2));
    this.membrosInfo.forEach(m => {
      this.valoresCustomizados[m.id] = perPessoa;
    });
  }

  atualizarValorMembro(id: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    this.valoresCustomizados[id] = Number(input.value);
  }

  somaCustomizada(): number {
    return Object.values(this.valoresCustomizados).reduce((acc, v) => acc + v, 0);
  }

  divisoesValidas(): boolean {
    if (!this.divisaoPersonalizada) return true;
    const valorTotal = Number(this.form.get('valor')?.value ?? 0);
    return Math.abs(this.somaCustomizada() - valorTotal) < 0.02;
  }

  // ── Painel lateral ────────────────────────────────────────────

  openPanel(despesa: DespesaView): void {
    this.isOpen             = !this.isOpen;
    this.despesaSelecionada = despesa;
  }

  closePanel(): void { this.isOpen = false; }

  // ── Pagar / desmarcar ─────────────────────────────────────────

  euJaPaguei(despesa: DespesaView): boolean {
    return despesa.pago.includes(this.usuarioId);
  }

  todosJaPagaram(despesa: DespesaView): boolean {
    if (despesa.divisoes.length === 0) return false;
    return despesa.divisoes.every(div => despesa.pago.includes(div.usuarioId));
  }

  minhaParte(despesa: DespesaView): number {
    const divisao = despesa.divisoes.find(d => d.usuarioId === this.usuarioId);
    return divisao ? divisao.valor : despesa.valor;
  }

  statusPagamentoPorMembro(despesa: DespesaView): { usuarioId: number; label: string; pago: boolean }[] {
    return despesa.divisoes.map(divisao => ({
      usuarioId: divisao.usuarioId,
      label: this.membrosInfo.find(m => m.id === divisao.usuarioId)?.label ?? `Usuário ${divisao.usuarioId}`,
      pago: despesa.pago.includes(divisao.usuarioId),
    }));
  }

  togglePago(despesa: DespesaView): void {
    const acao = this.euJaPaguei(despesa)
      ? this.despesaService.desmarcarComoPago(despesa.id, this.usuarioId)
      : this.despesaService.marcarComoPago(despesa.id, this.usuarioId);

    acao.subscribe({
      next: atualizada => {
        despesa.pago            = atualizada.pago ?? [];
        this.despesaSelecionada = { ...despesa };
        this.despesas.update(lista => [...lista]);
      },
      error: (err: ApiError) => this.erro.set(err.message ?? 'Erro ao atualizar pagamento.'),
    });
  }

  // ── Exclusão ──────────────────────────────────────────────────

  abrirConfirmDialog(despesa: DespesaView): void {
    this.despesaSelecionada = despesa;
    this.confirmDialog.nativeElement.showModal();
  }

  fecharConfirmDialog(): void { this.confirmDialog.nativeElement.close(); }

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

  // ── Helpers ───────────────────────────────────────────────────

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
