package com.coliv.coliv_backend.Modulos.Chat.Contratos.WebSocket;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;

public record WebSocketMensagemDTO(String contexto, MensagemResponseDTO mensagemDTO) {
}