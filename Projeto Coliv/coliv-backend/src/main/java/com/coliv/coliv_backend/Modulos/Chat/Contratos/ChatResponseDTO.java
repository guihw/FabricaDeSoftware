package com.coliv.coliv_backend.Modulos.Chat.Contratos;

import java.util.List;

public record ChatResponseDTO(Long id, List<Long> usuarioListId) {
}
