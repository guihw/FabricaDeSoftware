package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    public List<MensagemResponseDTO> listarPorUsuarioId(@PathVariable Long usuarioId,
                                                        @PathVariable TipoUsuario tipoUsuario) {

        return msgService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    @GetMapping("/buscarPorChatEUsuario/{usuarioId}/{chatId}/{tipoUsuario}")
    public List<MensagemResponseDTO> buscarPorChatEUsuario(@PathVariable Long usuarioId,@PathVariable Long chatId,
                                                        @PathVariable TipoUsuario tipoUsuario) {

        return msgService.buscarPorChatEUsuario(chatId, usuarioId, tipoUsuario);
    }

    @GetMapping("/buscarPorChat&Texto/{chatId}/{texto}")
    public List<MensagemResponseDTO> buscarPorChatETexto(@PathVariable Long chatId, @PathVariable String texto) {
        return msgService.buscarPorChatETexto(chatId, texto);
    }

    @PostMapping("/{usuarioId}/{chatId}/nova")
    public MensagemResponseDTO criarMensagem(@PathVariable Long usuarioId, @PathVariable Long chatId,
                                             @RequestBody MensagemRequestDTO dto) {
        return msgService.criarMensagem(usuarioId, chatId, dto);
    }

    @PutMapping("/editar/{sequencialId}/{chatId}/{usuarioId}")
    public MensagemResponseDTO editarMensagem(@PathVariable Long sequencialId, @PathVariable Long usuarioId,
                                              @PathVariable Long chatId, @RequestBody MensagemRequestDTO dto) {
        return msgService.editarMensagem(sequencialId, chatId, usuarioId, dto);
    }

    @DeleteMapping("/excluir/{sequencialId}/{chatId}/{usuarioId}")
    public void excluirMensagem (@PathVariable Long sequencialId, @PathVariable Long usuarioId,
                                 @PathVariable Long chatId) {
        msgService.excluirMensagem(sequencialId, chatId, usuarioId);
    }
}