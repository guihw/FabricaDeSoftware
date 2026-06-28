package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.StatusPagamento;

public record PixResponse(
        String billingId,
        String qrCodeBase64,
        String pixCopiaCola,
        StatusPagamento status
) {
}
