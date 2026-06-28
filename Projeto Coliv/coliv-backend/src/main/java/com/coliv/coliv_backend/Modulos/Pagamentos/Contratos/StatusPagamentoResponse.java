package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

public record StatusPagamentoResponse(
        String billingId,
        com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.StatusPagamento status
) {
}
