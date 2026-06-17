package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos;

import java.math.BigDecimal;
import java.util.List;

public record CardAnfitriaoResponseDTO(Long anfitriaoId, String nome, String email, String descricao,
                                       String localizacao, int quartos, Double classificacao,
                                       BigDecimal precoMensal, List<Long> arquivos) {
}
