package com.coliv.coliv_backend.Modulos.Security.Contratos;

public class CredenciaisInvalidas extends RuntimeException {
    public CredenciaisInvalidas(String message) {
        super(message);
    }
}
