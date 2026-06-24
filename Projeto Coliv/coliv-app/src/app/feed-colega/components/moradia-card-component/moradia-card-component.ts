import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecomendacaoCardAnfitriaoDTO } from '../../../core/services/recomendacao.service';

const COMODIDADE_LABEL: Record<string, string> = {
  wifi:       'Wi-Fi',
  academia:   'Academia',
  pet:        'Pet OK',
  silencioso: 'Silencioso',
  coworking:  'Coworking',
  piscina:    'Piscina',
  lavanderia: 'Lavanderia',
  limpeza:    'Limpeza',
  cafe:       'Café',
  rooftop:    'Rooftop',
};

@Component({
  selector: 'app-moradia-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './moradia-card-component.html',
  styleUrl: './moradia-card-component.css',
})
export class MoradiaCardComponent {
  @Input({ required: true }) recomendacao!: RecomendacaoCardAnfitriaoDTO;

  /** Dispara quando o usuário quer dar like (botão "Tenho Interesse") */
  @Output() like = new EventEmitter<RecomendacaoCardAnfitriaoDTO>();

  /** Dispara quando o usuário clica no card para ver detalhes */
  @Output() verDetalhes = new EventEmitter<RecomendacaoCardAnfitriaoDTO>();

  onLike(event: MouseEvent): void {
    event.stopPropagation(); 
    this.like.emit(this.recomendacao);
  }

  onCardClick(): void {
    this.verDetalhes.emit(this.recomendacao);
  }

  get card()   { return this.recomendacao.card; }
  get score()  { return this.recomendacao.score; }
  get resumo() { return this.recomendacao.resumoCompatibilidade; }

  get titulo(): string {
    const tipo = this.card.tipoVaga ?? 'Imóvel';
    const loc  = this.card.localizacao ?? '';
    return loc ? `${tipo}, ${loc}` : tipo;
  }

  get badgesComodidades(): string[] {
    const prioridade = ['pet','wifi','coworking','academia','piscina','silencioso','lavanderia','limpeza','cafe','rooftop'];
    return prioridade
      .filter(id => (this.card.comodidades ?? []).includes(id))
      .slice(0, 3)
      .map(id => COMODIDADE_LABEL[id] ?? id);
  }

  get corScore(): string {
    if (this.score >= 80) return 'bg-secondary-container text-on-secondary-container';
    if (this.score >= 60) return 'bg-primary-container text-on-primary-container';
    return 'bg-surface-container-high text-on-surface-variant';
  }

  get precoFormatado(): string {
    const v = this.card.precoMensal;
    if (!v || Number(v) === 0) return 'A consultar';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency', currency: 'BRL', minimumFractionDigits: 0,
    }).format(Number(v));
  }

  estrelas(n: number): boolean {
    return n <= Math.round(this.card.classificacao ?? 0);
  }
}