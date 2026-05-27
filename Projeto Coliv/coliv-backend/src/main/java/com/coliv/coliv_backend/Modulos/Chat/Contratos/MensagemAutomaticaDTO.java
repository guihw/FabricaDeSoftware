package com.coliv.coliv_backend.Modulos.Chat.Contratos;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

import java.time.LocalDateTime;

public record MensagemAutomaticaDTO(String texto, TipoUsuario tipoUsuario, Long chatId, LocalDateTime criadoEm,
                                    Long usuarioId) {
}
