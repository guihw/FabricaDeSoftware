import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { DadosImovelService } from '../core/services/dados-imovel.service';
import { DadosImovelDTO } from '../core/models/formulario.model';
import { ApiError } from '../core/services/api.service';
import { ArquivoService } from '../core/services/arquivo.service';
import { CardAnfitriaoService } from '../core/services/card-anfitriao.service';
import { Router } from '@angular/router';

interface Amenidade {
  id: string;
  label: string;
  icon: string;
  selecionada: boolean;
}

interface FotoSlot {
  id: number;
  arquivo: File | null;
  preview: string | null;
  principal: boolean;
}

@Component({
  selector: 'app-criar-anuncio',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './criar-anuncio.html',
})
export class CriarAnuncio implements OnInit {
  form!: FormGroup;

  fotos: FotoSlot[] = [
    { id: 0, arquivo: null, preview: null, principal: true  },
    { id: 1, arquivo: null, preview: null, principal: false },
    { id: 2, arquivo: null, preview: null, principal: false },
  ];

  dragIndex: number | null = null;
  dragOver: number | null = null;

  amenidades: Amenidade[] = [
    { id: 'wifi',       label: 'Wi-Fi 5G',        icon: 'wifi',                   selecionada: true  },
    { id: 'academia',   label: 'Academia',         icon: 'fitness_center',        selecionada: true  },
    { id: 'pet',        label: 'Pet Friendly',     icon: 'pets',                  selecionada: true  },
    { id: 'silencioso', label: 'Silencioso',       icon: 'volume_off',            selecionada: false },
    { id: 'coworking',  label: 'Coworking',        icon: 'work',                  selecionada: false },
    { id: 'piscina',    label: 'Piscina',          icon: 'pool',                  selecionada: false },
    { id: 'lavanderia', label: 'Lavanderia',       icon: 'local_laundry_service', selecionada: false },
    { id: 'limpeza',    label: 'Limpeza Semanal',  icon: 'cleaning_services',     selecionada: false },
    { id: 'cafe',       label: 'Café Gourmet',     icon: 'coffee',                selecionada: false },
    { id: 'rooftop',    label: 'Rooftop',          icon: 'deck',                  selecionada: false },
  ];

  tiposVaga = [
    'Quarto Privativo',
    'Quarto Compartilhado',
    'Suíte Master',
    'Studio Completo',
  ];

  modoEdicao = false;
  publicando = false;
  publicado = false;
  erroPublicacao: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dadosImovelService: DadosImovelService,
    private arquivoService: ArquivoService,
    private cardAnfitriaoService: CardAnfitriaoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      manifesto: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(3000)]],
      preco: [null, [Validators.required, Validators.min(1)]],
      tipoVaga: ['Quarto Privativo', Validators.required],
      bairro: ['', Validators.required],
      quartos: [1, [Validators.required, Validators.min(1)]],
    });

    const anfitriaoId = Number(sessionStorage.getItem('coliv_user_id'));
    if (anfitriaoId) {
      this.dadosImovelService.buscarPorAnfitriaoIdSeCompleto(anfitriaoId).pipe(
        catchError(() => of(null))
      ).subscribe(imovel => {
        if (!imovel) return;
        this.modoEdicao = true;
        this.form.patchValue({
          manifesto: imovel.descricao,
          preco: imovel.precoMensal,
          tipoVaga: imovel.tipoVaga,
          bairro: imovel.localizacao,
          quartos: imovel.quartos,
        });
        this.amenidades = this.amenidades.map(a => ({
          ...a,
          selecionada: imovel.comodidades.includes(a.id),
        }));
      });
    }
  }

  get fotosPreenchidas(): number {
    return this.fotos.filter(f => f.arquivo !== null).length;
  }

  get prontoParaPublicar(): boolean {
    return this.form?.valid && (this.modoEdicao || this.fotosPreenchidas > 0);
  }

  get amenidadesSelecionadas(): string[] {
    return this.amenidades.filter(a => a.selecionada).map(a => a.id);
  }

  publicar(): void {
    this.form.markAllAsTouched();

    if (!this.prontoParaPublicar || this.publicando) return;

    const anfitriaoId = Number(sessionStorage.getItem('coliv_user_id'));

    if (!anfitriaoId) {
      this.erroPublicacao = 'Sessão expirada. Faça login novamente.';
      return;
    }

    this.publicando = true;
    this.erroPublicacao = null;

    const dto: DadosImovelDTO = {
      descricao: this.form.value.manifesto,
      localizacao: this.form.value.bairro,
      quartos: this.form.value.quartos,
      precoMensal: Number(this.form.value.preco),
      tipoVaga: this.form.value.tipoVaga,
      comodidades: this.amenidadesSelecionadas,
    };

    const arquivos = this.fotos.filter(f => f.arquivo !== null).map(f => f.arquivo!);

    if (this.modoEdicao) {
      this.dadosImovelService.editar(anfitriaoId, dto).pipe(
        switchMap(() => arquivos.length > 0
          ? this.arquivoService.upload(arquivos).pipe(
              switchMap(arquivoDTOs =>
                this.cardAnfitriaoService.atualizarArquivos(anfitriaoId, arquivoDTOs.map(a => a.id))
              )
            )
          : of(null)
        )
      ).subscribe({
        next: () => { this.publicando = false; this.publicado = true; },
        error: (err: ApiError) => { this.publicando = false; this.erroPublicacao = err.message; },
      });
    } else {
      this.dadosImovelService.criar(anfitriaoId, dto).pipe(
        switchMap(() => this.arquivoService.upload(arquivos)),
        switchMap(arquivoDTOs =>
          this.cardAnfitriaoService.atualizarArquivos(anfitriaoId, arquivoDTOs.map(a => a.id))
        )
      ).subscribe({
        next: () => { this.publicando = false; this.publicado = true; },
        error: (err: ApiError) => { this.publicando = false; this.erroPublicacao = err.message; },
      });
    }
  }

  voltarParaEdicao(): void { this.publicado = false; }
  irParaGerenciarAnuncios(): void { this.router.navigate(['/gerenciaranuncios']); }
  voltar(): void{ 
    window.history.back(); 
  }

  // ── Fotos ─────────────────────────────────────────────────────

  triggerUpload(index: number): void {
    (document.getElementById(`upload-${index}`) as HTMLInputElement)?.click();
  }

  onFileSelected(event: Event, index: number): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file || !file.type.startsWith('image/')) return;

    const preview = URL.createObjectURL(file);
    this.fotos[index] = { ...this.fotos[index], arquivo: file, preview };
  }

  removerFoto(index: number, event: MouseEvent): void {
    event.stopPropagation();
    this.fotos[index] = { ...this.fotos[index], arquivo: null, preview: null };
  }

  onDragStart(index: number): void { this.dragIndex = index; }
  onDragOver(event: DragEvent, index: number): void { event.preventDefault(); this.dragOver = index; }
  onDragEnd(): void { this.dragIndex = null; this.dragOver = null; }

  onDrop(targetIndex: number): void {
    if (this.dragIndex === null || this.dragIndex === targetIndex) {
      this.dragIndex = null; this.dragOver = null; return;
    }
    const temp = { ...this.fotos[this.dragIndex] };
    this.fotos[this.dragIndex] = { ...this.fotos[targetIndex] };
    this.fotos[targetIndex] = temp;
    this.dragIndex = null; this.dragOver = null;
  }

  // ── Amenidades ────────────────────────────────────────────────

  toggleAmenidade(id: string): void {
    const item = this.amenidades.find(a => a.id === id);
    if (item) item.selecionada = !item.selecionada;
  }

  // ── Helpers ───────────────────────────────────────────────────

  campoInvalido(campo: string): boolean {
    const c = this.form.get(campo);
    return !!(c?.invalid && c?.touched);
  }

  iconFill(selecionada: boolean): string {
    return selecionada ? "'FILL' 1" : "'FILL' 0";
  }
}