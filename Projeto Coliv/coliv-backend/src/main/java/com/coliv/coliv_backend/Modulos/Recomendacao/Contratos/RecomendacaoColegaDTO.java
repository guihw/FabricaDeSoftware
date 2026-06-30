package com.coliv.coliv_backend.Modulos.Recomendacao.Contratos;

public record RecomendacaoColegaDTO(
        Long colegaId,
        String nome,
        String email,
        int score,
        String resumoCompatibilidade,
        String fotoPerfil
) {}
