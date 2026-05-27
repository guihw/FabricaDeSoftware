package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

public record MatchEventoDTO(Long anfitriaoId, Long colegaId, TipoUsuario iniciador) {
}
