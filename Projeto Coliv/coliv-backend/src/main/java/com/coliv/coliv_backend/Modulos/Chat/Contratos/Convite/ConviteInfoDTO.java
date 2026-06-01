package com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConviteInfoDTO(Long conviteId, Long chatId, BigDecimal valorDefinido, String condicoesIniciais,
                             LocalDateTime dataInicio) {
}
