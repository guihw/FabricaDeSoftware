package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

public record ColegaResponse(
        Long id,
        String nome,
        String email,
        String descricao, Float classificacao, Long preferenciaColegaId) {
}