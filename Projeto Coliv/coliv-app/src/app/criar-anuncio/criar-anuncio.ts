import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';

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
    { id: 'wifi', label: 'Wi-Fi 5G', icon: 'wifi', selecionada: true },
    { id: 'academia', label: 'Academia', icon: 'fitness_center', selecionada: true },
    { id: 'pet', label: 'Pet Friendly', icon: 'pets', selecionada: true },
    { id: 'silencioso', label: 'Silencioso', icon: 'volume_off', selecionada: false },
    { id: 'coworking', label: 'Coworking', icon: 'work', selecionada: false },
    { id: 'piscina', label: 'Piscina', icon: 'pool', selecionada: false },
    { id: 'lavanderia', label: 'Lavanderia', icon: 'local_laundry_service', selecionada: false },
    { id: 'limpeza', label: 'Limpeza Semanal', icon: 'cleaning_services', selecionada: false },
    { id: 'cafe', label: 'Café Gourmet', icon: 'coffee', selecionada: false },
    { id: 'rooftop', label: 'Rooftop', icon: 'deck', selecionada: false },
  ];

  tiposVaga = [
    'Quarto Privativo',
    'Quarto Compartilhado',
    'Suíte Master',
    'Studio Completo',
  ];

  publicando = false;
  publicado = false;
  erroPublicacao = false;

  get fotosPreenchidas(): number {
    return this.fotos.filter((f) => f.arquivo !== null).length;
  }

  get prontoParaPublicar(): boolean {
    return this.form?.valid && this.fotosPreenchidas > 0;
  }

  get amenidadesSelecionadas(): string[] {
    return this.amenidades.filter((a) => a.selecionada).map((a) => a.id);
  }

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      manifesto: ['', [Validators.required, Validators.minLength(50)]],
      preco: [null, [Validators.required, Validators.min(1)]],
      tipoVaga: ['Quarto Privativo', Validators.required],
      bairro: ['', Validators.required],
    });
  }

  // ── Fotos ──────────────────────────────────────────────────────────────────

  triggerUpload(index: number): void {
    const input = document.getElementById(`upload-${index}`) as HTMLInputElement;
    input?.click();
  }

  onFileSelected(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) return;

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

  // ── Amenidades ────────────────────────────────────────────────────────────

  toggleAmenidade(id: string): void {
    const item = this.amenidades.find((a) => a.id === id);
    if (item) item.selecionada = !item.selecionada;
  }

  // ── Publicação ────────────────────────────────────────────────────────────

  publicar(): void {
    if (!this.prontoParaPublicar || this.publicando) return;

    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.publicando = true;
    this.erroPublicacao = false;

    const payload = {
      ...this.form.value,
      amenidades: this.amenidadesSelecionadas,
      totalFotos: this.fotosPreenchidas,
    };

    console.log('Publicando anúncio:', payload);

    // Simula chamada à API
    setTimeout(() => {
      this.publicando = false;
      this.publicado = true;
    }, 2000);
  }

  voltarParaEdicao(): void {
    this.publicado = false;
  }

  voltar(): void {
    // Navegar para a rota anterior
    window.history.back();
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  campoInvalido(campo: string): boolean {
    const control = this.form.get(campo);
    return !!(control?.invalid && control?.touched);
  }


  iconFill(selecionada: boolean): string {
    return selecionada ? "'FILL' 1" : "'FILL' 0";
  }
  formatarPreco(valor: number | null): string {
    if (!valor) return '';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(valor);
  }
}