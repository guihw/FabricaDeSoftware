package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

public record MatchResponse(
        Long id,
        Long iniciador,
        Long colegaId,
        Long anfitriaoId
) {
}