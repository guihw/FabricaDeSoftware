package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

public record AbacateTransparentResponse(
        AbacateTransparentResultData data,
        Boolean success,
        String error
) {
}
