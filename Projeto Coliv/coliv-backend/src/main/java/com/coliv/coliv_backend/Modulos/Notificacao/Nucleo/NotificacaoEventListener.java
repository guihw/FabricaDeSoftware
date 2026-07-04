package com.coliv.coliv_backend.Modulos.Notificacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class NotificacaoEventListener {

    private final NotificacaoService service;

    NotificacaoEventListener(NotificacaoService service) {
        this.service = service;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNovaMensagem(NovaMensagemEvent event) {
        service.criar(
                event.destinatarioId(),
                TipoNotificacao.NOVA_MENSAGEM,
                "Nova mensagem",
                "Você recebeu uma nova mensagem",
                event.chatId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNovoMatch(NovoMatchEvent event) {
        service.criar(
                event.anfitriaoId(),
                TipoNotificacao.NOVO_MATCH,
                "Novo match!",
                "Você tem um novo match disponível",
                event.matchId()
        );
        service.criar(
                event.colegaId(),
                TipoNotificacao.NOVO_MATCH,
                "Novo match!",
                "Você tem um novo match disponível",
                event.matchId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDespesaAdicionada(DespesaAdicionadaEvent event) {
        service.criar(
                event.usuarioId(),
                TipoNotificacao.DESPESA_ADICIONADA,
                "Nova despesa",
                "Nova divisão de R$ " + String.format("%.2f", event.valor()) + " atribuída a você",
                event.despesaId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConviteRecebido(ConviteRecebidoEvent event) {
        service.criar(
                event.colegaId(),
                TipoNotificacao.CONVITE_RECEBIDO,
                "Convite de moradia",
                "Você recebeu um convite de moradia",
                event.conviteId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConviteAceito(ConviteAceitoEvent event) {
        service.criar(
                event.anfitriaoId(),
                TipoNotificacao.CONVITE_ACEITO,
                "Convite aceito!",
                "Seu convite de moradia foi aceito",
                event.conviteId()
        );
    }
}
