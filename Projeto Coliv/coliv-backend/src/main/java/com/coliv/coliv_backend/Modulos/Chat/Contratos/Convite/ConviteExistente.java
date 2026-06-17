package com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConviteExistente extends RuntimeException {
    public ConviteExistente() {
        super("Convite já existe\n");
    }
}