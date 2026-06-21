package com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite;

public class ConviteStatusException extends RuntimeException {
    public ConviteStatusException(ConviteStatus status) {
        super("Convite já modificado.\nStatus : " + status + "\n");
    }
}