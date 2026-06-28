package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

/**
 * Dados retornados dentro de "data" pelo POST /v1/pixQrCode/create
 * e também pelo GET de checagem de status.
 * status pode ser: PENDING | PAID | EXPIRED | CANCELLED.
 */
public record AbacatePixData(
        String id,
        String status,
        String brCode,
        String brCodeBase64,
        Integer amount,
        String expiresAt
) {
}
