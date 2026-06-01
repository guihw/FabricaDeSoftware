package com.coliv.coliv_backend.Modulos.Arquivos.Contratos;

public class ArquivoNaoEncontrado extends RuntimeException {

    public ArquivoNaoEncontrado(Long id) {
        super("Arquivo não encontrado. Id: " + id);
    }
}