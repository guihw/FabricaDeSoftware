package com.coliv.coliv_backend.Modulos.Arquivos.Contratos;

public class QuantidadeArquivosInvalida extends RuntimeException {

    public QuantidadeArquivosInvalida() {
        super("É permitido enviar no máximo 10 arquivos.");
    }
}