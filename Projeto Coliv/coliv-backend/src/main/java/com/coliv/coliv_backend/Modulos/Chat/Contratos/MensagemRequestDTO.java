package com.coliv.coliv_backend.Modulos.Chat.Contratos;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

public record MensagemRequestDTO(String texto, TipoUsuario tipoUsuario, Long arquivoId) {
}
