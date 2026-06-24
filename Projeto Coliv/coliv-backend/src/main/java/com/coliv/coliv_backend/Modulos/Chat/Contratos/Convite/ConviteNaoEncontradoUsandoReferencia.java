package com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite;

public class ConviteNaoEncontradoUsandoReferencia extends RuntimeException {
    public ConviteNaoEncontradoUsandoReferencia(Long id) {
        super("\nNenhum Convite encontrado utilizando o id " + id + " como Referência\n");
    }
}
