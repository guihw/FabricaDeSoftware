package com.coliv.coliv_backend.Modulos.Notificacao.Contratos;

import com.coliv.coliv_backend.Modulos.Notificacao.Nucleo.TipoNotificacao;
import java.time.LocalDateTime;

public record NotificacaoDTO(
        Long id,
        Long usuarioId,
        TipoNotificacao tipo,
        String titulo,
        String mensagem,
        Long referenciaId,
        boolean lida,
        LocalDateTime criadoEm
) {}
