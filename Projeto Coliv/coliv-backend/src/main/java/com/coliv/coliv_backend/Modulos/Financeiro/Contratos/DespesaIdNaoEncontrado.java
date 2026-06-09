package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

public class DespesaIdNaoEncontrado extends RuntimeException {

    public DespesaIdNaoEncontrado(Long id) {
        super("Despesa com id " + id + " não encontrada.");
    }
}