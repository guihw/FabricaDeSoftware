package com.coliv.coliv_backend.Modulos.Arquivos.Contratos;

public class TipoArquivoInvalido extends RuntimeException {

    public TipoArquivoInvalido(String tipo) {
        super("Tipo de arquivo não permitido: " + tipo);
    }
}