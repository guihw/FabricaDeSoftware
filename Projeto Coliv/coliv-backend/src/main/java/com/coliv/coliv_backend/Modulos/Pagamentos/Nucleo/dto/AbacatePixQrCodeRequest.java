package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Corpo da requisição para POST /v1/pixQrCode/create.
 * amount é em centavos (único campo obrigatório). expiresIn é em segundos.
 * Não enviamos customer: é opcional na API e não precisamos dele.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AbacatePixQrCodeRequest(
        Integer amount,
        Integer expiresIn,
        String description
) {
}
