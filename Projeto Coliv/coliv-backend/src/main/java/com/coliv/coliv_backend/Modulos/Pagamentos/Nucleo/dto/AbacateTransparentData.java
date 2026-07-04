package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Vai dentro de "data" no corpo de POST /v2/transparents/create.
 * amount em centavos. expiresIn em segundos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AbacateTransparentData(
        Integer amount,
        Integer expiresIn,
        String description
) {
}
