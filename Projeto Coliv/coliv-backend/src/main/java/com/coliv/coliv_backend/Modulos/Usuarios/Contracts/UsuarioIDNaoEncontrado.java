package com.coliv.coliv_backend.Modulos.Usuarios.Contracts;

public class UsuarioIDNaoEncontrado extends RuntimeException {
    public UsuarioIDNaoEncontrado(Long id) {
        super("Usuário de Id " + id + " não encontrado");
    }
}
