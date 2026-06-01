package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

public class MensagemNaoEncontradaUsandoReferencia extends RuntimeException {
    public MensagemNaoEncontradaUsandoReferencia(Long id) {
        super("Nenhuma Mensagem encontrada utilizando o id " + id + " como Referência");
    }
}
