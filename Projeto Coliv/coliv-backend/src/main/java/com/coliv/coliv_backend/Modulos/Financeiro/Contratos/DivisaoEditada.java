package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

public record DivisaoEditada(
        Long divisaoId,
        Long despesaId,
        Long usuarioId,
        Double valor
) {
}