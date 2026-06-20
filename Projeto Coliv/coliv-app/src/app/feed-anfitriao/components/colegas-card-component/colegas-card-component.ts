import { Component, Input, Output, EventEmitter, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecomendacaoColegaDTO } from '../../../core/services/recomendacao.service';

type DirecaoSaida = 'direita' | 'esquerda' | null;

@Component({
  selector: 'app-colegas-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './colegas-card-component.html',
  styleUrl: './colegas-card-component.css',
})
export class ColegasCardComponent {

  @Input({ required: true }) recomendacao!: RecomendacaoColegaDTO;

  @Output() aceitar  = new EventEmitter<RecomendacaoColegaDTO>();
  @Output() recusar  = new EventEmitter<RecomendacaoColegaDTO>();

  direcaoSaida = signal<DirecaoSaida>(null);

  get score() {
    return this.recomendacao.score;
  }

  get resumo() {
    return this.recomendacao.resumoCompatibilidade;
  }

  get iniciais(): string {
    return this.recomendacao.nome
      .split(' ')
      .slice(0, 2)
      .map(p => p[0]?.toUpperCase() ?? '')
      .join('');
  }

  get corScore(): string {
    if (this.score >= 80) return 'bg-secondary-container text-on-secondary-container';
    if (this.score >= 60) return 'bg-primary-container text-on-primary-container';
    return 'bg-surface-container-high text-on-surface-variant';
  }

  onAceitar(): void {
    if (this.direcaoSaida()) return; 
    this.direcaoSaida.set('direita');
    setTimeout(() => this.aceitar.emit(this.recomendacao), 350);
  }

  onRecusar(): void {
    if (this.direcaoSaida()) return;
    this.direcaoSaida.set('esquerda');
    setTimeout(() => this.recusar.emit(this.recomendacao), 350);
  }
}