package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

import java.time.LocalDateTime;

/**
 * <p>ADD Documentation Later</p>
 * @param texto
 * @param chatId
 * @param criadoEm
 */
public record MensagemAutomaticaDTO(String texto, Long chatId, LocalDateTime criadoEm) {
}