// ──────────────────────────────────────────────────────────────
// Modelos de Despesa/Divisão
// ──────────────────────────────────────────────────────────────

// Espelha: DespesaDTO.java
export interface DespesaDTO {
  valor: number;
  dataVencimento: string;
  descricao: string;
  anfitriaoId: number;
}

// Espelha: DespesaResponse.java (entidade Despesa)
export interface Despesa {
  id: number;
  valor: number;
  dataVencimento: string;
  descricao: string;
  pago: number[]; // lista de usuarioId que já pagaram
  anfitriaoId: number;
}

// Espelha: DivisaoDTO.java
export interface DivisaoDTO {
  despesaId: number;
  usuarioId: number;
  arquivoId: number | null;
  valor: number;
}

// Espelha: DivisaoResponse.java
export interface Divisao {
  id: number;
  despesaId: number;
  usuarioId: number;
  arquivoId: number | null;
  valor: number;
}

export type TipoDespesa = 'coletiva' | 'individual';

export interface DespesaView {
  id: number;
  nome: string; 
  valor: number; 
  dataVencimento: string;
  categoria: string;
  tipodeDespesa: TipoDespesa;
  divisoes: Divisao[];
  pago: number[];
}