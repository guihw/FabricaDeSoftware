package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

public class MensagemNaoEncontradaUsandoReferencias extends RuntimeException {
    public MensagemNaoEncontradaUsandoReferencias(Long sequencialId, Long chatId, Long usuarioId) {
        super("Mensagem não encontrada usando as seguintes referências\nsequencialId = " +
                sequencialId + "\nchatId = " + chatId + "\nusuarioId = " + usuarioId +
                "\nVerifique a existência das referências disponibilizadas");
    }
}
