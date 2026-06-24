// ──────────────────────────────────────────────────────────────
// Modelos de Formulários — alinhados com DTOs do backend
// ──────────────────────────────────────────────────────────────

// Espelha: PreferenciasAnfitriaoDTO.java
export interface PreferenciasAnfitriaoDTO {
  presencaAnimais: boolean;
  horariosParaVisita: string;
  politicaDeLimpeza: string;
  regrasDaCasa: string;
  perfilColegaDesejado: string;
}

export interface DadosImovelDTO {
  descricao: string;
  localizacao: string;
  quartos: number;
  precoMensal: number;
  tipoVaga: string;
  comodidades: string[];
}


export interface DadosImovel extends DadosImovelDTO {
  id: number;
  anfitriaoId: number;
}


export interface PreferenciasAnfitriao extends PreferenciasAnfitriaoDTO {
  id: number;
  anfitriaoId: number;
}