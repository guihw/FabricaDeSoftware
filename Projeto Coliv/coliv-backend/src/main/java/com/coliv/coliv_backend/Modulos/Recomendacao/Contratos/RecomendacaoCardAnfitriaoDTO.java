package com.coliv.coliv_backend.Modulos.Recomendacao.Contratos;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;

public record RecomendacaoCardAnfitriaoDTO(CardAnfitriaoResponseDTO card, int score, String resumoCompatibilidade) {
}
