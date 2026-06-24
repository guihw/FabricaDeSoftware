package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos;

import java.math.BigDecimal;
import java.util.List;

//DTO de leitura dos dados do imóvel (usado internamente entre módulos).
public record DadosImovelDTO(
        Long anfitriaoId,
        String descricao,
        String localizacao,
        int quartos,
        BigDecimal precoMensal,
        String tipoVaga,
        List<String> comodidades
) {
}