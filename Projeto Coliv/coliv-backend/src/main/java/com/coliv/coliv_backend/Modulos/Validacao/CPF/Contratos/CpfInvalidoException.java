package com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos;

public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String cpf) {
        super("CPF \"" + cpf + "\" possui formato inválido");
    }
}
