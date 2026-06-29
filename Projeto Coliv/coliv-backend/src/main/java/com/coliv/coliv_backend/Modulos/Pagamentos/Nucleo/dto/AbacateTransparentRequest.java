package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

public record AbacateTransparentRequest(
        String method,
        AbacateTransparentData data
) {
    public static AbacateTransparentRequest pix(AbacateTransparentData data) {
        return new AbacateTransparentRequest("PIX", data);
    }
}
