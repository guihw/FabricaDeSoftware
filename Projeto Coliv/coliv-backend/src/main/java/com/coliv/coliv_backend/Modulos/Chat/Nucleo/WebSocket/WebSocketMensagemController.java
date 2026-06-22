package com.coliv.coliv_backend.Modulos.Chat.Nucleo.WebSocket;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.WebSocket.WebSocketMensagemDTO;
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

    @MessageMapping("/chat/mensagem/nova/{usuarioId}/{chatId}")
    @SendTo("/queue/chat/{chatId}")
    private WebSocketMensagemDTO novaMensagemDireta(@DestinationVariable Long usuarioId, @DestinationVariable Long chatId,
                                                    @Payload MensagemRequestDTO dto) {

        return new WebSocketMensagemDTO("NOVA_MENSAGEM", msgDiretaService.criarMensagem(usuarioId, chatId, dto));
    }

    @MessageMapping("/chat/grupo/mensagem/nova/{usuarioId}/{grupoId}")
    @SendTo("/topic/chat/grupo/{grupoId}")
    private WebSocketMensagemDTO novaMensagemGrupo(@DestinationVariable Long usuarioId, @DestinationVariable Long grupoId,
                                             @Payload MensagemRequestDTO dto) {

        return new WebSocketMensagemDTO("NOVA_MENSAGEM", msgGrupoService.criarMensagem(usuarioId, grupoId, dto));
    }

    @MessageMapping("/chat/mensagem/editar/{sequencialId}/{chatId}/{usuarioId}")
    @SendTo("queue/chat/{chatId}")
    private WebSocketMensagemDTO editarMensagemDireta(
            @DestinationVariable Long sequencialId,
            @DestinationVariable Long chatId,
            @DestinationVariable Long usuarioId,
            @Payload MensagemRequestDTO dto) {

        return new WebSocketMensagemDTO("MENSAGEM_ATUALIZADA", msgDiretaService.editarMensagem(sequencialId,
                chatId, usuarioId, dto));
    }

    @MessageMapping("/chat/grupo/mensagem/editar/{sequencialId}/{chatId}/{usuarioId}")
    @SendTo("queue/chat/grupo/{grupoId}")
    private WebSocketMensagemDTO editarMensagemGrupo(
            @DestinationVariable Long sequencialId,
            @DestinationVariable Long grupoId,
            @DestinationVariable Long usuarioId,
            @Payload MensagemRequestDTO dto) {

        return new WebSocketMensagemDTO("MENSAGEM_ATUALIZADA", msgGrupoService.editarMensagem(sequencialId,
                grupoId, usuarioId, dto));
    }
}