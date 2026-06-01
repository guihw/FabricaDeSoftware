package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatResponseDTO;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEventoDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin("*")
class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/listar")
    private List<ChatResponseDTO> listarChats() {
        return chatService.listarChats();
    }

    @GetMapping("/listar/{usuarioId}/{tipoUsuario}")
    private List<ChatResponseDTO> listarChatsPorUsuario(@PathVariable Long usuarioId,
                                                        @PathVariable TipoUsuario tipoUsuario) {
        return chatService.listarChatsPorUsuario(usuarioId, tipoUsuario);
    }

    @GetMapping("/buscar/{id}")
    private ChatResponseDTO buscarPorId(@PathVariable Long id) {
        return chatService.buscarPorId(id);
    }

    @DeleteMapping("/remover/{id}")
    private void excluir(@PathVariable Long id) {
        chatService.excluir(id);
    }

    //Não esquecer de remover quando criar a classe de Match
    @PostMapping("/match/event/teste")
    private void matchEventTeste(@RequestBody MatchEventoDTO dto) {
        chatService.matchEventTeste(dto);
    }
}
