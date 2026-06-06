package com.coliv.coliv_backend.Modulos.Matchmaking.Contratos;

public class MatchIdNaoEncontrado extends RuntimeException {

    public MatchIdNaoEncontrado(Long id) {
        super("Match com id " + id + " não encontrado.");
    }
}