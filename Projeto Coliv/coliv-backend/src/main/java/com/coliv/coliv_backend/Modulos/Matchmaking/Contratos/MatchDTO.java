package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

public record MatchDTO(
        TipoUsuario iniciador,
        Long colegaId,
        Long anfitriaoId
) {
}