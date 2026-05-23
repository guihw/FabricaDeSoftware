import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { DadosImovelService } from '../core/services/dados-imovel.service';
import { DadosImovelDTO } from '../core/models/formulario.model';
import { ApiError } from '../core/services/api.service';

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
    { id: 0, arquivo: null, preview: null, principal: true },
    { id: 1, arquivo: null, preview: null, principal: false },
    { id: 2, arquivo: null, preview: null, principal: false },
  ];

  dragIndex: number | null = null;
  dragOver: number | null = null;

  amenidades: Amenidade[] = [
    { id: 'wifi',       label: 'Wi-Fi 5G',       icon: 'wifi',                  selecionada: true },
    { id: 'academia',   label: 'Academia',        icon: 'fitness_center',        selecionada: true },
    { id: 'pet',        label: 'Pet Friendly',    icon: 'pets',                  selecionada: true },
    { id: 'silencioso', label: 'Silencioso',      icon: 'volume_off',            selecionada: false },
    { id: 'coworking',  label: 'Coworking',       icon: 'work',                  selecionada: false },
    { id: 'piscina',    label: 'Piscina',         icon: 'pool',                  selecionada: false },
    { id: 'lavanderia', label: 'Lavanderia',      icon: 'local_laundry_service', selecionada: false },
    { id: 'limpeza',    label: 'Limpeza Semanal', icon: 'cleaning_services',     selecionada: false },
    { id: 'cafe',       label: 'Café Gourmet',    icon: 'coffee',                selecionada: false },
    { id: 'rooftop',    label: 'Rooftop',         icon: 'deck',                  selecionada: false },
  ];

  tiposVaga = [
    'Quarto Privativo',
    'Quarto Compartilhado',
    'Suíte Master',
    'Studio Completo',
  ];

  publicando = false;
  publicado = false;
  erroPublicacao: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dadosImovelService: DadosImovelService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      manifesto:  ['', [Validators.required, Validators.minLength(50)]],
      preco:      [null, [Validators.required, Validators.min(1)]],
      tipoVaga:   ['Quarto Privativo', Validators.required],
      bairro:     ['', Validators.required],
    });
  }

  get fotosPreenchidas(): number {
    return this.fotos.filter((f) => f.arquivo !== null).length;
  }

  get prontoParaPublicar(): boolean {
    return this.form?.valid && this.fotosPreenchidas > 0;
  }

  get amenidadesSelecionadas(): string[] {
    return this.amenidades.filter((a) => a.selecionada).map((a) => a.id);
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

    // O backend recebe: descricao, localizacao, quartos
    // Mapeamos os campos do formulário para o DTO
    const dto: DadosImovelDTO = {
      descricao:   this.form.value.manifesto,
      localizacao: this.form.value.bairro,
      quartos:     1, // TODO: adicionar campo de quartos ao formulário
    };

    this.dadosImovelService.criar(anfitriaoId, dto).subscribe({
      next: () => {
        this.publicando = false;
        this.publicado = true;
      },
      error: (err: ApiError) => {
        this.publicando = false;
        this.erroPublicacao = err.message;
      },
    });
  }

  voltarParaEdicao(): void {
    this.publicado = false;
  }

  voltar(): void {
    window.history.back();
  }

  // ── Fotos ─────────────────────────────────────────────────

  triggerUpload(index: number): void {
    const input = document.getElementById(`upload-${index}`) as HTMLInputElement;
    input?.click();
  }

  onFileSelected(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file || !file.type.startsWith('image/')) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      this.fotos[index] = {
        ...this.fotos[index],
        arquivo: file,
        preview: e.target?.result as string,
      };
    };
    reader.readAsDataURL(file);
  }

  removerFoto(index: number, event: MouseEvent): void {
    event.stopPropagation();
    this.fotos[index] = { ...this.fotos[index], arquivo: null, preview: null };
  }

  onDragStart(index: number): void {
    this.dragIndex = index;
  }

  onDragOver(event: DragEvent, index: number): void {
    event.preventDefault();
    this.dragOver = index;
  }

  onDrop(targetIndex: number): void {
    if (this.dragIndex === null || this.dragIndex === targetIndex) {
      this.dragIndex = null;
      this.dragOver = null;
      return;
    }
    const temp = { ...this.fotos[this.dragIndex] };
    this.fotos[this.dragIndex] = { ...this.fotos[targetIndex] };
    this.fotos[targetIndex] = temp;
    this.dragIndex = null;
    this.dragOver = null;
  }

  onDragEnd(): void {
    this.dragIndex = null;
    this.dragOver = null;
  }

  // ── Amenidades ───────────────────────────────────────────

  toggleAmenidade(id: string): void {
    const item = this.amenidades.find((a) => a.id === id);
    if (item) item.selecionada = !item.selecionada;
  }

  // ── Helpers ──────────────────────────────────────────────

  campoInvalido(campo: string): boolean {
    const control = this.form.get(campo);
    return !!(control?.invalid && control?.touched);
  }

  iconFill(selecionada: boolean): string {
    return selecionada ? "'FILL' 1" : "'FILL' 0";
  }
}
