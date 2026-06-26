package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "Chats entre colega e anfitrião. Endpoints públicos (não requerem token).")
@RestController
@RequestMapping("/chat")
@CrossOrigin("*")
class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "Listar todos os chats")
    @SecurityRequirements
    @GetMapping("/listar")
    public List<ChatResponseDTO> listarChats() {
        return chatService.listarChats();
    }

    @Operation(summary = "Listar chats por usuário")
    @SecurityRequirements
    @GetMapping("/listar/{usuarioId}/{tipoUsuario}")
    public List<ChatResponseDTO> listarChatsPorUsuario(@PathVariable Long usuarioId,
                                                       @PathVariable TipoUsuario tipoUsuario) {
        return chatService.listarChatsPorUsuario(usuarioId, tipoUsuario);
    }

    @Operation(summary = "Buscar chat por ID")
    @SecurityRequirements
    @GetMapping("/buscar/{id}")
    public ChatResponseDTO buscarPorId(@PathVariable Long id) {
        return chatService.buscarPorId(id);
    }

    @Operation(summary = "Remover chat")
    @SecurityRequirements
    @DeleteMapping("/remover/{id}")
    public void excluir(@PathVariable Long id) {
        chatService.excluir(id);
    }
}
