// ──────────────────────────────────────────────────────────────
// Modelos alinhados com os DTOs e entidades do backend Spring Boot
// ──────────────────────────────────────────────────────────────

// Espelha: AnfitriaoDTO.java
export interface AnfitriaoDTO {
  nome: string;
  cpf: string;
  email: string;
  senha: string;
  fotoPerfil?: number | null;
}

// Espelha: CreateColegaRequest.java
export interface CreateColegaRequest {
  nome: string;
  email: string;
  password: string;
  cpf: string
}

// Espelha: ColegaResponse.java  (retorno da criação)
export interface ColegaResponse {
  id: number;
  nome: string;
  email: string;
}

// Espelha: UsuarioDTO.java  (retorno interno, ex: getuser)
export interface UsuarioDTO {
  nome: string;
  email: string;
}

// Espelha: Anfitriao.java (entidade completa, retorno do listar/buscar)
export interface Anfitriao {
  id: number;
  nome: string;
  cpf: string;
  email: string;
  possuiPlano: boolean;
  fotoPerfil: number | null;
}

// Espelha: Colega.java (entidade completa)
export interface Colega {
  id: number;
  nome: string;
  email: string;
  descricao?: string;
  classificacao?: number;
}
