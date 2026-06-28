package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

/**
 * Envelope padrão das respostas da AbacatePay: { "data": {...}, "error": ... }
 */
public record AbacatePixQrCodeResponse(
        AbacatePixData data,
        String error
) {
}
