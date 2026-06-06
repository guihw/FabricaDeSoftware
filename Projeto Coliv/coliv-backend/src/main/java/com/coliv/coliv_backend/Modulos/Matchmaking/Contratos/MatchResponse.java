package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

import com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo.StatusMatch;

public record MatchResponse(
        Long id,
        Long colegaId,
        Long anfitriaoId,
        StatusMatch status
) {
}