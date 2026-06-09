package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

import java.time.LocalDateTime;
import java.util.List;

public record DespesaResponse(
        Long id,
        Double valor,
        LocalDateTime dataVencimento,
        String descricao,
        List<Long> pago
) {
}