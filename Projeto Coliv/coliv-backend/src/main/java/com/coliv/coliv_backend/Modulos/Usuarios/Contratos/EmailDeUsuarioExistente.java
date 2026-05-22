package com.coliv.coliv_backend.Modulos.Usuarios.Contratos;

public class EmailDeUsuarioExistente extends RuntimeException {
    public EmailDeUsuarioExistente(String email) {
        super("Email \"" + email + "\" já existe");
    }
}
