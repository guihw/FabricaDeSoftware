package com.coliv.coliv_backend.Modulos.Avaliacao.Contratos;

public class AvaliacaoIdNaoEncontrado extends RuntimeException {

    public AvaliacaoIdNaoEncontrado(Long id) {
        super("Avaliação com id " + id + " não encontrada.");
    }
}