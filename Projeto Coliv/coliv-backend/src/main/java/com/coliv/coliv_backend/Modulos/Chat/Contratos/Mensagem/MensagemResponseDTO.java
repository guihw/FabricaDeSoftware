package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

import java.time.LocalDateTime;

public record MensagemResponseDTO(Long id, String texto, TipoUsuario tipoUsuario, LocalDateTime criadoEm,
                                  Long arquivoId, Long usuarioId) {
}
