package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

/**
 * Dados retornados dentro de "data" pelo POST /v2/transparents/create
 * quando method = "PIX".
 * status pode ser: PENDING | PAID | EXPIRED | CANCELLED (mesmo conjunto do v1).
 */
public record AbacateTransparentResultData(
        String id,
        Integer amount,
        String status,
        Boolean devMode,
        String brCode,
        String brCodeBase64,
        Integer platformFee,
        String createdAt,
        String updatedAt,
        String expiresAt
) {
}
