package com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo;

public class GrupoIDNaoEncontrado extends RuntimeException {
    public GrupoIDNaoEncontrado(Long id) {
        super("Grupo de id " + id + " não encontrado\n");
    }
}
