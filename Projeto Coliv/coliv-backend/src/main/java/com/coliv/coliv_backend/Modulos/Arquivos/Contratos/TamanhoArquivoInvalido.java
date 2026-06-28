package com.coliv.coliv_backend.Modulos.Arquivos.Contratos;

public class TamanhoArquivoInvalido extends RuntimeException {

    public TamanhoArquivoInvalido(String nome) {
        super("Arquivo excede o limite de 5MB: " + nome);
    }
}