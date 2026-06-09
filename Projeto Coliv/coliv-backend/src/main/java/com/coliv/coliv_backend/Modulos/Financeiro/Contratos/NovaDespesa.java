package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

public record NovaDespesa(
        Long despesaId,
        Double valor,
        String descricao
) {
}