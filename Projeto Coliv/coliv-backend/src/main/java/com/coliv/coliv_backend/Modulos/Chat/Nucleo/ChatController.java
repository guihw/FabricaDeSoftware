package com.coliv.coliv_backend.Modulos.Chat.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.ChatRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.ChatResponseDTO;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEvento;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEventoDTO;
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

    @GetMapping("/buscar/{id}")
    private ChatResponseDTO buscarPorId(@PathVariable Long id) {
        return chatService.buscarPorId(id);
    }

    @PostMapping("/novo")
    private ChatResponseDTO criarChat(@RequestBody ChatRequestDTO dto) {
        return chatService.criarChat(dto);
    }

    @PatchMapping("/editar/{id}")
    private ChatResponseDTO editarChat(@PathVariable Long id, ChatRequestDTO dto) {
        return chatService.editarChat(id, dto);
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
