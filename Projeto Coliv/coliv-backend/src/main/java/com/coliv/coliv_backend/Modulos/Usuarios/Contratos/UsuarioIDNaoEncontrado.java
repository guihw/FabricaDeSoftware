package com.coliv.coliv_backend.Modulos.Usuarios.Contratos;

public class UsuarioIDNaoEncontrado extends RuntimeException {
    public UsuarioIDNaoEncontrado(Long id) {
        super("Usuario de id "+ id +" não encontrado");
    }
}
