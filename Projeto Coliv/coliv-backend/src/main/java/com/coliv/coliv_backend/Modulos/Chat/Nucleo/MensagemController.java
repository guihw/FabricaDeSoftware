package com.coliv.coliv_backend.Modulos.Chat.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/mensagem")
@CrossOrigin ("*")
class MensagemController {

    @Autowired
    private MensagemService msgService;

    @GetMapping("/listar")
    public List<MensagemResponseDTO> listar() {
        return msgService.listar();
    }

    @GetMapping("/buscarPorChatId/{chatId}")
    public List<MensagemResponseDTO> listarPorChat(@PathVariable Long chatId) {
        return msgService.buscarPorChatId(chatId);
    }

    @PostMapping("/buscarPorUsuarioId/{usuarioId}")
    public List<MensagemResponseDTO> listarPorUsuarioId(@PathVariable Long usuarioId,
                                                        @RequestBody MensagemRequestDTO dto) {

        return msgService.buscarPorUsuarioId(usuarioId, dto.tipoUsuario());
    }

    @PostMapping("buscarPorChatEUsuario/{usuarioId}/{chatId}")
    public List<MensagemResponseDTO> buscarPorChatEUsuario(@PathVariable Long usuarioId,@PathVariable Long chatId,
                                                        @RequestBody MensagemRequestDTO dto) {

        return msgService.buscarPorChatEUsuario(chatId, usuarioId, dto.tipoUsuario());
    }

    @PostMapping("/{usuarioId}/{chatId}/nova")
    public MensagemResponseDTO criarMensagem(@PathVariable Long usuarioId, @PathVariable Long chatId,
                                             @RequestBody MensagemRequestDTO dto) {
        return msgService.criarMensagem(usuarioId, chatId, dto);
    }
}