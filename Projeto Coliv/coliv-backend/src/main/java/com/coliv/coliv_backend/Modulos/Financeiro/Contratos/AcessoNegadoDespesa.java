package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Usuário não pertence a esta casa")
public class AcessoNegadoDespesa extends RuntimeException {
    public AcessoNegadoDespesa() {
        super("Usuário não pertence a esta casa\n");
    }
}
