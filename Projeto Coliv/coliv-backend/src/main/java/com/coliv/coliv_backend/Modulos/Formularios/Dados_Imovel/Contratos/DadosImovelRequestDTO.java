package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos;

import java.math.BigDecimal;
import java.util.List;

public record DadosImovelRequestDTO(
        String descricao,
        String localizacao,
        int quartos,
        BigDecimal precoMensal,
        String tipoVaga,
        List<String> comodidades
) {
}