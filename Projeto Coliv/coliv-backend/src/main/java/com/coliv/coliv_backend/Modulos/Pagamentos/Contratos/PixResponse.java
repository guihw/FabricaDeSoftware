package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

public record PixResponse(

        String paymentId,

        String qrCode,

        String pixCopiaCola,

        StatusPagamento status

) {
}