package com.coliv.coliv_backend.Modulos.Chat.Nucleo.WebSocket;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta.MensagemDiretaService;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo.MensagemGrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMensagemController {

    @Autowired
    private MensagemDiretaService msgDiretaService;
    @Autowired
    private MensagemGrupoService msgGrupoService;

    @MessageMapping("/chat/mensagem/nova/{usuarioId}/{matchId}")
    @SendTo("/queue/chat/{matchId}")
    private MensagemResponseDTO novaMensagemDireta(@DestinationVariable Long usuarioId, @DestinationVariable Long chatId,
                                             @Payload MensagemRequestDTO dto) {
        return msgDiretaService.criarMensagem(usuarioId, chatId, dto);
    }

    @MessageMapping("/chat/grupo/mensagem/nova/{usuarioId}/{grupoId}")
    @SendTo("/topic/chat/grupo/{grupoId}")
    private MensagemResponseDTO novaMensagemGrupo(@DestinationVariable Long usuarioId, @DestinationVariable Long grupoId,
                                             @Payload MensagemRequestDTO dto) {
        return msgGrupoService.criarMensagem(usuarioId, grupoId, dto);
    }
}