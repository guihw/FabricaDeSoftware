package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/grupo/mensagem")
@CrossOrigin("*")
public class MensagemGrupoController {

    @Autowired
    private MensagemGrupoService msgService;

    @GetMapping("/listar")
    private List<MensagemResponseDTO> listar() {
        return msgService.listar();
    }

    @GetMapping("/buscarPorGrupoId/{grupoId}")
    private List<MensagemResponseDTO> buscarPorGrupoId(@PathVariable Long grupoId) {
        return msgService.buscarPorGrupoId(grupoId);
    }

    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    private List<MensagemResponseDTO> buscarPorUsuarioId(@PathVariable Long usuarioId, @PathVariable TipoUsuario
            tipoUsuario) {
        return msgService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    @GetMapping("/buscarPorGrupoEUsuario/{grupoId}/{usuarioId}/{tipoUsuario}")
    private List<MensagemResponseDTO> buscarPorGrupoEUsuario (@PathVariable Long grupoId, @PathVariable Long usuarioId,
                                                              @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorGrupoEUsuario(grupoId, usuarioId, tipoUsuario);
    }

    @GetMapping("/buscarPorChatETexto/{grupoId}/{texto}")
    private List<MensagemResponseDTO> buscarPorChatETexto(@PathVariable Long grupoId, @PathVariable String texto) {
        return msgService.buscarPorChatETexto(grupoId, texto);
    }

    @PostMapping("/nova/{grupoId}/{usuarioId}")
    private MensagemResponseDTO novaMensagem(@PathVariable Long grupoId, @PathVariable Long usuarioId,
                                             @RequestBody MensagemRequestDTO dto) {
        return msgService.criarMensagem(usuarioId, grupoId, dto);
    }

    @PutMapping("/editar/{sequencialId}/{grupoId}/{usuarioId}")
    private MensagemResponseDTO editarMensagem(@PathVariable Long sequencialId, @PathVariable Long grupoId,
                                               @PathVariable Long usuarioId, @RequestBody MensagemRequestDTO dto) {
        return msgService.editarMensagem(sequencialId, grupoId, usuarioId, dto);
    }

    @DeleteMapping("/excluir/{sequencialId}/{grupoId}/{usuarioId}")
    private void excluirMensagem(@PathVariable Long sequencialId, @PathVariable Long grupoId, @PathVariable Long
            usuarioId) {
        msgService.excluirMensagem(sequencialId, grupoId, usuarioId);
    }
}