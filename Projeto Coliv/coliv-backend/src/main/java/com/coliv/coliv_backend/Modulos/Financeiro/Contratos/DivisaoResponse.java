package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

public record DivisaoResponse(
        Long id,
        Long despesaId,
        Long usuarioId,
        Long arquivoId,
        Double valor
) {
}