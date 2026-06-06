package com.coliv.coliv_backend.Modulos.Avaliacao.Contratos;

public class ComentarioIdNaoEncontrado extends RuntimeException {

    public ComentarioIdNaoEncontrado(Long id) {
        super("Comentário com id " + id + " não encontrado.");
    }
}