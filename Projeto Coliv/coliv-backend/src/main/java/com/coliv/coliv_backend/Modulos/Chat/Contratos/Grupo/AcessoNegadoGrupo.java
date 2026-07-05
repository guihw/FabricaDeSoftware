package com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Ação não permitida sobre este grupo")
public class AcessoNegadoGrupo extends RuntimeException {
    public AcessoNegadoGrupo() {
        super("Ação não permitida sobre este grupo\n");
    }
}
