package com.coliv.coliv_backend.Modulos.Chat.Contratos;

public class ChatIDNaoEncontrado extends RuntimeException {
    public ChatIDNaoEncontrado(Long id) {
        super("Chat de id "+ id +" não encontrado");
    }
}
