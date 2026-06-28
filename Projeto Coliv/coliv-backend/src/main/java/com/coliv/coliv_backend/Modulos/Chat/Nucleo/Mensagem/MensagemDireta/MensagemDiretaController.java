package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemNaoEncontradaUsandoReferencias;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mensagens Diretas", description = "Mensagens trocadas em chats 1-a-1 entre colega e anfitrião. Endpoints públicos.")
@RestController
@RequestMapping("/chat/mensagem")
@CrossOrigin("*")
class MensagemDiretaController {

    @Autowired
    private MensagemDiretaService msgService;

    @Operation(summary = "Listar todas as mensagens diretas")
    @SecurityRequirements
    @GetMapping("/listar")
    public List<MensagemResponseDTO> listar() {
        return msgService.listar();
    }

    @Operation(summary = "Listar mensagens por chat")
    @SecurityRequirements
    @GetMapping("/buscarPorChatId/{chatId}")
    public List<MensagemResponseDTO> listarPorChat(@PathVariable Long chatId) {
        return msgService.buscarPorChatId(chatId);
    }

    @Operation(summary = "Listar mensagens por usuário")
    @SecurityRequirements
    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    public List<MensagemResponseDTO> listarPorUsuarioId(@PathVariable Long usuarioId,
                                                        @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    @Operation(summary = "Listar mensagens por chat e usuário")
    @SecurityRequirements
    @GetMapping("/buscarPorChatEUsuario/{usuarioId}/{chatId}/{tipoUsuario}")
    public List<MensagemResponseDTO> buscarPorChatEUsuario(@PathVariable Long usuarioId,
                                                           @PathVariable Long chatId,
                                                           @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorChatEUsuario(chatId, usuarioId, tipoUsuario);
    }

    @Operation(summary = "Buscar mensagens por texto", description = "Filtra mensagens de um chat que contêm o texto fornecido")
    @SecurityRequirements
    @GetMapping("/buscarPorChatETexto/{chatId}/{texto}")
    public List<MensagemResponseDTO> buscarPorChatETexto(@PathVariable Long chatId, @PathVariable String texto) {
        return msgService.buscarPorChatETexto(chatId, texto);
    }

    @Operation(summary = "Enviar mensagem direta")
    @SecurityRequirements
    @PostMapping("/{usuarioId}/{chatId}/nova")
    public MensagemResponseDTO criarMensagem(@PathVariable Long usuarioId, @PathVariable Long chatId,
                                             @RequestBody MensagemRequestDTO dto) {
        return msgService.criarMensagem(usuarioId, chatId, dto);
    }

    @Operation(summary = "Editar mensagem direta")
    @SecurityRequirements
    @PutMapping("/editar/{sequencialId}/{chatId}/{usuarioId}")
    public MensagemResponseDTO editarMensagem(@PathVariable Long sequencialId, @PathVariable Long usuarioId,
                                              @PathVariable Long chatId, @RequestBody MensagemRequestDTO dto) {
        return msgService.editarMensagem(sequencialId, chatId, usuarioId, dto);
    }

    @Operation(summary = "Excluir mensagem direta")
    @SecurityRequirements
    @DeleteMapping("/excluir/{sequencialId}/{chatId}/{usuarioId}")
    public void excluirMensagem(@PathVariable Long sequencialId, @PathVariable Long usuarioId,
                                @PathVariable Long chatId) {
        msgService.excluirMensagem(sequencialId, chatId, usuarioId);
    }
}
