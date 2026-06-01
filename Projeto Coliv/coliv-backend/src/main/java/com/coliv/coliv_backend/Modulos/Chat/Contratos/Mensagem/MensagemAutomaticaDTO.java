package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

import java.time.LocalDateTime;

public record MensagemAutomaticaDTO(String texto, TipoUsuario tipoUsuario, Chat chat, LocalDateTime criadoEm,
                                    Long usuarioId) {
}
