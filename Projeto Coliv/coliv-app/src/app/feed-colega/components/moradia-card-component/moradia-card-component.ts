import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecomendacaoCardAnfitriaoDTO } from '../../../core/services/recomendacao.service';

@Component({
  selector: 'app-moradia-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './moradia-card-component.html',
  styleUrl: './moradia-card-component.css',
})
export class MoradiaCardComponent {

  @Input({ required: true }) recomendacao!: RecomendacaoCardAnfitriaoDTO;
  @Output() like = new EventEmitter<RecomendacaoCardAnfitriaoDTO>();
 
  onLike(): void {
    this.like.emit(this.recomendacao);
  }

  get card() {
    return this.recomendacao.card;
  }

  get score() {
    return this.recomendacao.score;
  }

  get resumo() {
    return this.recomendacao.resumoCompatibilidade;
  }


  get corScore(): string {
    if (this.score >= 80) return 'bg-secondary-container text-on-secondary-container';
    if (this.score >= 60) return 'bg-primary-container text-on-primary-container';
    return 'bg-surface-container-high text-on-surface-variant';
  }


  get precoFormatado(): string {
    if (!this.card.precoMensal) return 'A consultar';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 0,
    }).format(this.card.precoMensal);
  }

  estrelas(n: number): boolean {
    return n <= Math.round(this.card.classificacao ?? 0);
  }
}