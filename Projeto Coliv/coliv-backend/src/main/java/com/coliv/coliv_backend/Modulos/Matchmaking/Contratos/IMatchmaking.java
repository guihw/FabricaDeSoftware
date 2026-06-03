package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

import java.util.List;

public interface IMatchmaking {

    List<MatchResponse> listar();

    MatchResponse buscar(Long id);
}