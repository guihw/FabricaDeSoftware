import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecomendacaoColegaDTO } from '../../../core/services/recomendacao.service';

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

  get score() {
    return this.recomendacao.score;
  }

  get resumo() {
    return this.recomendacao.resumoCompatibilidade;
  }

  /** Iniciais do nome para avatar placeholder */
  get iniciais(): string {
    return this.recomendacao.nome
      .split(' ')
      .slice(0, 2)
      .map(p => p[0]?.toUpperCase() ?? '')
      .join('');
  }

  /** Classe de cor do badge de score */
  get corScore(): string {
    if (this.score >= 80) return 'bg-secondary-container text-on-secondary-container';
    if (this.score >= 60) return 'bg-primary-container text-on-primary-container';
    return 'bg-surface-container-high text-on-surface-variant';
  }

  onAceitar(): void {
    this.aceitar.emit(this.recomendacao);
  }

  onRecusar(): void {
    this.recusar.emit(this.recomendacao);
  }
}