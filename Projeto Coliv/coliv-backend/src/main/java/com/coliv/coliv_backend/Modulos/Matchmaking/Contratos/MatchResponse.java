package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

import com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo.StatusMatch;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

public record MatchResponse(
        Long id,
        Long colegaId,
        Long anfitriaoId,
        StatusMatch status,
        TipoUsuario iniciador
) {
}