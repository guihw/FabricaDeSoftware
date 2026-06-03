package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

public record MatchEvent(
        Long iniciador,
        Long colegaId,
        Long anfitriaoId
) {
}