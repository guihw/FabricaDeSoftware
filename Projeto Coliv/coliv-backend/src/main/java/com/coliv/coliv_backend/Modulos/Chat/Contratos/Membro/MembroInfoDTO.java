package com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

public record MembroInfoDTO(Long usuarioId, TipoUsuario tipoUsuario) {
}