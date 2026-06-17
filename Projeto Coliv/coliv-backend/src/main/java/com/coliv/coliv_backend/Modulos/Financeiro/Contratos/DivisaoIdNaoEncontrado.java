package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

public class DivisaoIdNaoEncontrado extends RuntimeException {

    public DivisaoIdNaoEncontrado(Long id) {
        super("Divisão com id " + id + " não encontrada.");
    }
}