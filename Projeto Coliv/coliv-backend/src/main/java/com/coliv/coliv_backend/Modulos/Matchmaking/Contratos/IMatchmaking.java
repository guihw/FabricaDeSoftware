package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

import java.util.List;

public interface IMatchmaking {

    List<MatchResponse> listar();

    MatchResponse buscar(Long id);

    Long getUserId(Long id, TipoUsuario tipoUsuario);
}