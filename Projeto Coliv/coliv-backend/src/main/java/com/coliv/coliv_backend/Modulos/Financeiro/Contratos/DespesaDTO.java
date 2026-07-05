package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

import java.time.LocalDateTime;

public record DespesaDTO(
        Double valor,
        LocalDateTime dataVencimento,
        String descricao,
        Long anfitriaoId
) {
}