package com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite;

import java.time.LocalDateTime;

public record ConviteInfoDTO(
        Long conviteId,
        Long chatId,
        String texto,
        LocalDateTime dataInicio,
        LocalDateTime atualizadoEm
) {
}