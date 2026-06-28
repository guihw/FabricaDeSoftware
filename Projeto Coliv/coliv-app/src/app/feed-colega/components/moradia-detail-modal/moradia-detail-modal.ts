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
import { RecomendacaoCardAnfitriaoDTO } from '../../../core/services/recomendacao.service';

// Mapa id → label completo para a tela de detalhe
const COMODIDADE_INFO: Record<string, { label: string; icon: string }> = {
  wifi:       { label: 'Wi-Fi 5G',        icon: 'wifi'                  },
  academia:   { label: 'Academia',         icon: 'fitness_center'        },
  pet:        { label: 'Pet Friendly',     icon: 'pets'                  },
  silencioso: { label: 'Ambiente Silencioso', icon: 'volume_off'         },
  coworking:  { label: 'Coworking',        icon: 'work'                  },
  piscina:    { label: 'Piscina',          icon: 'pool'                  },
  lavanderia: { label: 'Lavanderia',       icon: 'local_laundry_service' },
  limpeza:    { label: 'Limpeza Semanal',  icon: 'cleaning_services'     },
  cafe:       { label: 'Café Gourmet',     icon: 'coffee'                },
  rooftop:    { label: 'Rooftop',          icon: 'deck'                  },
};

@Component({
  selector: 'app-moradia-detail-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './moradia-detail-modal.html',
  styleUrl: './moradia-detail-modal.css',
})
export class MoradiaDetailModalComponent implements OnChanges {
  @Input() recomendacao: RecomendacaoCardAnfitriaoDTO | null = null;
  @Input() aberto = false;

  @Output() fechar   = new EventEmitter<void>();
  @Output() interesse = new EventEmitter<RecomendacaoCardAnfitriaoDTO>();

  descricaoExpandida = false;

  ngOnChanges(changes: SimpleChanges): void {
    // Reseta estado ao abrir novo card
    if (changes['recomendacao']) {
      this.descricaoExpandida = false;
    }
    // Bloqueia scroll do body quando o modal está aberto
    if (changes['aberto']) {
      document.body.style.overflow = this.aberto ? 'hidden' : '';
    }
  }

  // Fecha ao clicar no backdrop
  fecharModal(): void {
    this.fechar.emit();
  }

  onInteresse(): void {
    if (this.recomendacao) {
      this.interesse.emit(this.recomendacao);
      this.fechar.emit();
    }
  }

  toggleDescricao(): void {
    this.descricaoExpandida = !this.descricaoExpandida;
  }

  // Fecha com Esc
  @HostListener('document:keydown.escape')
  onEsc(): void {
    if (this.aberto) this.fechar.emit();
  }

  get card() { return this.recomendacao?.card; }
  get score() { return this.recomendacao?.score ?? 0; }
  get resumo() { return this.recomendacao?.resumoCompatibilidade ?? ''; }

  get titulo(): string {
    const tipo = this.card?.tipoVaga ?? 'Imóvel';
    const loc  = this.card?.localizacao ?? '';
    return loc ? `${tipo}, ${loc}` : tipo;
  }

  get precoFormatado(): string {
    const v = this.card?.precoMensal;
    if (!v || Number(v) === 0) return 'A consultar';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency', currency: 'BRL', minimumFractionDigits: 0,
    }).format(Number(v));
  }

  get corScore(): string {
    if (this.score >= 80) return 'bg-secondary-container text-on-secondary-container';
    if (this.score >= 60) return 'bg-primary-container text-on-primary-container';
    return 'bg-surface-container-high text-on-surface-variant';
  }

  get comodidadesDetalhadas(): { label: string; icon: string }[] {
    return (this.card?.comodidades ?? [])
      .map(id => COMODIDADE_INFO[id])
      .filter(Boolean);
  }

  get inicialAnfitriao(): string {
    return (this.card?.nome ?? 'A').charAt(0).toUpperCase();
  }

  estrelas(n: number): boolean {
    return n <= Math.round(this.card?.classificacao ?? 0);
  }

  stopPropagation(e: Event): void { e.stopPropagation(); }
}