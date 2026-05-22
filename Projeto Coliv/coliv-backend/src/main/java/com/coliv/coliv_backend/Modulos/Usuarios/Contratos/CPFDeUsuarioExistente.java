package com.coliv.coliv_backend.Modulos.Usuarios.Contratos;

public class CPFDeUsuarioExistente extends RuntimeException {
    public CPFDeUsuarioExistente() {
        super("CPF já existe");
    }
}
