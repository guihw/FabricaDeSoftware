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

// Espelha: DadosImovelDTO.java
export interface DadosImovelDTO {
  descricao: string;
  localizacao: string;
  quartos: number;
}

// Resposta completa de DadosImovel (entidade)
export interface DadosImovel extends DadosImovelDTO {
  id: number;
  anfitriaoId: number;
}

// Resposta completa de PreferenciasAnfitriao (entidade)
export interface PreferenciasAnfitriao extends PreferenciasAnfitriaoDTO {
  id: number;
  anfitriaoId: number;
}
