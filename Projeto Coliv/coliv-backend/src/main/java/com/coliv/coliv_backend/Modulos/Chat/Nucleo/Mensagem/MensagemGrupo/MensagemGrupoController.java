package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mensagens de Grupo", description = "Mensagens trocadas em grupos de moradores. Endpoints públicos.")
@RestController
@RequestMapping("/chat/grupo/mensagem")
@CrossOrigin("*")
public class MensagemGrupoController {

    @Autowired
    private MensagemGrupoService msgService;

    @Operation(summary = "Listar todas as mensagens de grupo")
    @SecurityRequirements
    @GetMapping("/listar")
    public List<MensagemResponseDTO> listar() {
        return msgService.listar();
    }

    @Operation(summary = "Listar mensagens por grupo")
    @SecurityRequirements
    @GetMapping("/buscarPorGrupoId/{grupoId}")
    public List<MensagemResponseDTO> buscarPorGrupoId(@PathVariable Long grupoId) {
        return msgService.buscarPorGrupoId(grupoId);
    }

    @Operation(summary = "Listar mensagens por usuário")
    @SecurityRequirements
    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    public List<MensagemResponseDTO> buscarPorUsuarioId(@PathVariable Long usuarioId,
                                                        @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    @Operation(summary = "Listar mensagens por grupo e usuário")
    @SecurityRequirements
    @GetMapping("/buscarPorGrupoEUsuario/{grupoId}/{usuarioId}/{tipoUsuario}")
    public List<MensagemResponseDTO> buscarPorGrupoEUsuario(@PathVariable Long grupoId, @PathVariable Long usuarioId,
                                                            @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorGrupoEUsuario(grupoId, usuarioId, tipoUsuario);
    }

    @Operation(summary = "Buscar mensagens de grupo por texto")
    @SecurityRequirements
    @GetMapping("/buscarPorChatETexto/{grupoId}/{texto}")
    public List<MensagemResponseDTO> buscarPorChatETexto(@PathVariable Long grupoId, @PathVariable String texto) {
        return msgService.buscarPorChatETexto(grupoId, texto);
    }

    @Operation(summary = "Enviar mensagem no grupo")
    @SecurityRequirements
    @PostMapping("/nova/{grupoId}/{usuarioId}")
    public MensagemResponseDTO novaMensagem(@PathVariable Long grupoId, @PathVariable Long usuarioId,
                                            @RequestBody MensagemRequestDTO dto) {
        return msgService.criarMensagem(usuarioId, grupoId, dto);
    }

    @Operation(summary = "Editar mensagem de grupo")
    @SecurityRequirements
    @PutMapping("/editar/{sequencialId}/{grupoId}/{usuarioId}")
    public MensagemResponseDTO editarMensagem(@PathVariable Long sequencialId, @PathVariable Long grupoId,
                                              @PathVariable Long usuarioId, @RequestBody MensagemRequestDTO dto) {
        return msgService.editarMensagem(sequencialId, grupoId, usuarioId, dto);
    }

    @Operation(summary = "Excluir mensagem de grupo")
    @SecurityRequirements
    @DeleteMapping("/excluir/{sequencialId}/{grupoId}/{usuarioId}")
    public void excluirMensagem(@PathVariable Long sequencialId, @PathVariable Long grupoId,
                                @PathVariable Long usuarioId) {
        msgService.excluirMensagem(sequencialId, grupoId, usuarioId);
    }
}
