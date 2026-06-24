package com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo;

public class GrupoNaoEncontradoUsandoReferencia extends RuntimeException {
    public GrupoNaoEncontradoUsandoReferencia(Long id) {
        super("Nenhum Grupo encontrado utilizando o id " + id + " como Referência");
    }
}
