import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

import { PreferenciasAnfitriaoService } from '../core/services/preferencias-anfitriao.service';
import { PreferenciasAnfitriaoDTO } from '../core/models/formulario.model';

import {
  PreferenciasColegaService,
  PreferenciasColegaDTO,
  NivelDeSociabilidade,
  NivelDeLimpeza,
  HabitoDeTrabalho,
} from '../core/services/preferencias-colega.service';

import { ArquivoService } from '../core/services/arquivo.service';
import { ColegaService } from '../core/services/colega.service';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { FotoPerfilService } from '../core/services/foto-perfil.service';
import { ApiError } from '../core/services/api.service';

// ── Tipos locais para as seleções visuais ─────────────────────
type RitmoSono     = 'MADRUGADOR' | 'EQUILIBRADO' | 'NOTURNO';
type Sociabilidade = 'INTROVERTIDO' | 'MODERADO' | 'SOCIAVEL';
type Limpeza       = 'BAIXO' | 'MEDIO' | 'ALTO';
type Trabalho      = 'HOME_OFFICE' | 'HIBRIDO' | 'PRESENCIAL';

// Mapa ritmo de sono → horário LocalTime compatível com backend
const HORARIO_SONO: Record<RitmoSono, string> = {
  MADRUGADOR: '06:00:00',
  EQUILIBRADO: '23:00:00',
  NOTURNO:    '01:00:00',
};

// Mapa ritmo de sono → horário de visita para anfitrião
const HORARIO_VISITA: Record<RitmoSono, string> = {
  MADRUGADOR: '09:00:00',
  EQUILIBRADO: '14:00:00',
  NOTURNO:    '20:00:00',
};

@Component({
  selector: 'app-form-preferencias',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterOutlet, RouterLink],
  templateUrl: './form-preferencias.html',
  styleUrl: './form-preferencias.css',
})
export class FormPreferencias implements OnInit {

  // ── Tipo de usuário ────────────────────────────────────────
  isAnfitriao = signal(false);
  userId      = signal<number | null>(null);

  // ── Estado do formulário ───────────────────────────────────
  form!: FormGroup;
  carregando    = signal(false);
  erro          = signal<string | null>(null);
  sucesso       = signal(false);
  uploadandoFoto = signal(false);
  private fotoPerfilService = inject(FotoPerfilService);
  fotoPerfilUrl  = this.fotoPerfilService.fotoPerfilUrl;

  // ── Seleções visuais (estado real ligado ao envio) ─────────
  ritmoSono     = signal<RitmoSono>('EQUILIBRADO');
  sociabilidade = signal<Sociabilidade>('MODERADO');
  limpeza       = signal<Limpeza>('MEDIO');
  trabalho      = signal<Trabalho>('HIBRIDO');
  aceitaPets    = signal(false);

  // ── Labels para exibição ───────────────────────────────────
  readonly opcoesRitmo: { valor: RitmoSono; label: string; icone: string }[] = [
    { valor: 'MADRUGADOR', label: 'Madrugador',  icone: 'wb_sunny'  },
    { valor: 'EQUILIBRADO', label: 'Equilibrado', icone: 'balance'   },
    { valor: 'NOTURNO',    label: 'Noturno',     icone: 'bedtime'   },
  ];

  readonly opcoesSociabilidade: { valor: Sociabilidade; label: string; icone: string }[] = [
    { valor: 'INTROVERTIDO', label: 'Prefiro meu espaço',     icone: 'person'    },
    { valor: 'MODERADO',     label: 'Equilíbrio é tudo',      icone: 'people'    },
    { valor: 'SOCIAVEL',     label: 'Amo jantares coletivos', icone: 'diversity_3' },
  ];

  readonly opcoesLimpeza: { valor: Limpeza; label: string; icone: string }[] = [
    { valor: 'BAIXO',  label: 'Caos criativo',        icone: 'auto_awesome' },
    { valor: 'MEDIO',  label: 'Organizado na medida', icone: 'done'         },
    { valor: 'ALTO',   label: 'Impecável',             icone: 'cleaning_services' },
  ];

  readonly opcoesTrabalho: { valor: Trabalho; label: string; icone: string }[] = [
    { valor: 'HOME_OFFICE', label: 'Home Office',      icone: 'home_work'   },
    { valor: 'HIBRIDO',     label: 'Híbrido',          icone: 'sync_alt'    },
    { valor: 'PRESENCIAL',  label: 'Trabalho Externo', icone: 'corporate_fare' },
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private preferenciasAnfitriaoService: PreferenciasAnfitriaoService,
    private preferenciasColegaService: PreferenciasColegaService,
    private arquivoService: ArquivoService,
    private colegaService: ColegaService,
    private anfitriaoService: AnfitriaoService,
  ) {}

  ngOnInit(): void {
    const tipo = sessionStorage.getItem('coliv_user_tipo');
    const id   = sessionStorage.getItem('coliv_user_id');

    this.isAnfitriao.set(tipo === 'anfitriao');
    this.userId.set(id ? Number(id) : null);

    this.form = this.fb.group({
      localizacao:   ['', Validators.required],
      precoMin:      [800,  [Validators.required, Validators.min(0)]],
      precoMax:      [4500, [Validators.required, Validators.min(0)]],
      // Campos exclusivos de anfitrião
      politicaLimpeza:     [''],
      regrasDaCasa:        [''],
      perfilColegaDesejado: [''],
    });
  }

  // ── Helpers para template ──────────────────────────────────

  ativo<T>(signal: () => T, valor: T): boolean {
    return signal() === valor;
  }

  campoInvalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c?.invalid && c?.touched);
  }

  togglePets(): void {
    this.aceitaPets.update(v => !v);
  }

  // ── Upload de foto ─────────────────────────────────────────

  onFotoSelecionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const id = this.userId();
    if (!id) return;

    this.uploadandoFoto.set(true);
    this.erro.set(null);

    this.arquivoService.upload([input.files[0]]).subscribe({
      next: (arquivos) => {
        if (!arquivos || arquivos.length === 0) return;
        const arquivo = arquivos[0];
        this.fotoPerfilService.cachear(arquivo.url);

        const dados: any = { fotoPerfilId: arquivo.id };
        const onDone = () => this.uploadandoFoto.set(false);
        if (this.isAnfitriao()) {
          this.anfitriaoService.editar(id, dados).subscribe({ next: onDone, error: onDone });
        } else {
          this.colegaService.editar(id, dados).subscribe({ next: onDone, error: onDone });
        }
      },
      error: (err: ApiError) => {
        this.uploadandoFoto.set(false);
        this.erro.set(err.message ?? 'Erro ao enviar foto.');
      },
    });
  }

  // ── Envio ──────────────────────────────────────────────────

  salvar(): void {
    this.form.markAllAsTouched();

    if (!this.form.valid) return;

    const id = this.userId();
    if (!id) {
      this.erro.set('Sessão expirada. Faça o cadastro novamente.');
      return;
    }

    this.carregando.set(true);
    this.erro.set(null);

    if (this.isAnfitriao()) {
      this.salvarAnfitriao(id);
    } else {
      this.salvarColega(id);
    }
  }

  private salvarAnfitriao(anfitriaoId: number): void {
    const dto: PreferenciasAnfitriaoDTO = {
      presencaAnimais:      this.aceitaPets(),
      horariosParaVisita:   HORARIO_VISITA[this.ritmoSono()],
      politicaDeLimpeza:    this.form.value.politicaLimpeza  || this.limpeza(),
      regrasDaCasa:         this.form.value.regrasDaCasa     || this.sociabilidade(),
      perfilColegaDesejado: this.form.value.perfilColegaDesejado || this.trabalho(),
    };

    this.preferenciasAnfitriaoService.criar(anfitriaoId, dto).subscribe({
      next: () => {
        this.carregando.set(false);
        this.sucesso.set(true);
        // Anfitrião ainda não tem anúncio publicado nesse ponto do fluxo,
        // então segue direto para a criação do anúncio em vez do feed.
        setTimeout(() => this.router.navigate(['/criaranuncio']), 800);
      },
      error: (err: ApiError) => {
        this.carregando.set(false);
        this.erro.set(err.message);
      },
    });
  }

  private salvarColega(colegaId: number): void {
    const { precoMin, precoMax, localizacao } = this.form.value;

    if (precoMin > precoMax) {
      this.carregando.set(false);
      this.erro.set('O preço mínimo não pode ser maior que o máximo.');
      return;
    }

    const dto: PreferenciasColegaDTO = {
      precoMinimo:          precoMin,
      precoMaximo:          precoMax,
      localizacao:          localizacao,
      horarioDeSono:        HORARIO_SONO[this.ritmoSono()],
      nivelDeSociabilidade: this.sociabilidade() as NivelDeSociabilidade,
      nivelDeLimpeza:       this.limpeza()       as NivelDeLimpeza,
      habitoDeTrabalho:     this.trabalho()      as HabitoDeTrabalho,
      aceitaAnimais:        this.aceitaPets(),
    };

    this.preferenciasColegaService.criar(colegaId, dto).subscribe({
      next: () => {
        this.carregando.set(false);
        this.sucesso.set(true);
        setTimeout(() => this.router.navigate(['/feedcolega']), 800);
      },
      error: (err: ApiError) => {
        this.carregando.set(false);
        this.erro.set(err.message);
      },
    });
  }
}