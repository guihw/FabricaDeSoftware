import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecomendacaoColegaDTO } from '../../../core/services/recomendacao.service';

// Mapeia valores do backend para labels legíveis
const SOCIABILIDADE: Record<string, { label: string; icon: string }> = {
  INTROVERTIDO: { label: 'Prefere ambientes calmos',    icon: 'person'     },
  MODERADO:     { label: 'Equilíbrio social',            icon: 'people'     },
  SOCIAVEL:     { label: 'Gosta de convivência',         icon: 'diversity_3' },
};

const LIMPEZA: Record<string, { label: string; icon: string }> = {
  BAIXO:  { label: 'Organizado a seu modo',  icon: 'auto_awesome'      },
  MEDIO:  { label: 'Organizado na medida',   icon: 'done'              },
  ALTO:   { label: 'Muito organizado',        icon: 'cleaning_services' },
};

const TRABALHO: Record<string, { label: string; icon: string }> = {
  HOME_OFFICE: { label: 'Home Office',     icon: 'home_work'      },
  HIBRIDO:     { label: 'Híbrido',         icon: 'sync_alt'       },
  PRESENCIAL:  { label: 'Trabalho externo', icon: 'corporate_fare' },
};

@Component({
  selector: 'app-colega-detail-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './colega-detail-modal.html',
  styleUrl: './colega-detail-modal.css',
})
export class ColegaDetailModalComponent implements OnChanges {
  @Input() recomendacao: RecomendacaoColegaDTO | null = null;
  @Input() aberto = false;
  /** Anfitrião já demonstrou interesse mas o colega ainda não confirmou */
  @Input() interesse = false;

  @Output() fechar  = new EventEmitter<void>();
  @Output() aceitar = new EventEmitter<RecomendacaoColegaDTO>();
  @Output() recusar = new EventEmitter<RecomendacaoColegaDTO>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['aberto']) {
      document.body.style.overflow = this.aberto ? 'hidden' : '';
    }
  }

  fecharModal(): void { this.fechar.emit(); }

  onAceitar(): void {
    if (this.recomendacao) {
      this.aceitar.emit(this.recomendacao);
      this.fechar.emit();
    }
  }

  onRecusar(): void {
    if (this.recomendacao) {
      this.recusar.emit(this.recomendacao);
      this.fechar.emit();
    }
  }

  stopPropagation(e: Event): void { e.stopPropagation(); }

  @HostListener('document:keydown.escape')
  onEsc(): void { if (this.aberto) this.fechar.emit(); }

  get score()  { return this.recomendacao?.score ?? 0; }
  get resumo() { return this.recomendacao?.resumoCompatibilidade ?? ''; }

  get corScore(): string {
    if (this.score >= 80) return 'bg-secondary-container text-on-secondary-container';
    if (this.score >= 60) return 'bg-primary-container text-on-primary-container';
    return 'bg-surface-container-high text-on-surface-variant';
  }

  get iniciais(): string {
    return (this.recomendacao?.nome ?? 'C')
      .split(' ').slice(0, 2)
      .map(p => p[0]?.toUpperCase() ?? '')
      .join('');
  }

  // Helpers de detalhe de preferências (vindos do score do backend)
  get tituloResumo(): string {
    const score = this.score;
    if (score >= 80) return 'Excelente compatibilidade';
    if (score >= 60) return 'Boa compatibilidade';
    if (score >= 40) return 'Compatibilidade moderada';
    return 'Compatibilidade baixa';
  }
}