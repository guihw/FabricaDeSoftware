package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Convites de Chat", description = "Convites para iniciar um chat entre colega e anfitrião após o match. Endpoints públicos.")
@RestController
@RequestMapping("/chat/convite")
@CrossOrigin("*")
public class ConviteController {

    @Autowired
    private ConviteService conviteService;

    @Operation(summary = "Listar convites por usuário")
    @SecurityRequirements
    @GetMapping("/listarPorUsuario/{usuarioId}/{tipoUsuario}")
    public List<ConviteResponseDTO> listarPorUsuario(@PathVariable Long usuarioId,
                                                     @PathVariable TipoUsuario tipoUsuario) {
        return conviteService.listarPorUsuario(usuarioId, tipoUsuario);
    }

    @Operation(summary = "Buscar convite mais recente por match")
    @SecurityRequirements
    @GetMapping("/buscarConviteRecente/{matchId}")
    public ConviteResponseDTO buscarConviteRecente(@PathVariable Long matchId) {
        return conviteService.buscarConviteRecente(matchId);
    }

    @Operation(summary = "Enviar convite de chat", description = "Cria um convite com status PENDENTE para o match informado")
    @SecurityRequirements
    @PostMapping("/enviar/{matchId}")
    public ConviteResponseDTO enviarConvite(@PathVariable Long matchId, @RequestBody ConviteRequestDTO dto) {
        return conviteService.novoConvite(ConviteStatus.PENDENTE, dto, matchId);
    }

    @Operation(summary = "Aceitar convite", description = "Atualiza o status do convite para ACEITO. Exige que o status atual seja PENDENTE.")
    @SecurityRequirements
    @PatchMapping("/aceito/{matchId}")
    public void conviteAceito(@PathVariable Long matchId) {
        conviteService.conviteAceito(matchId);
    }

    @Operation(summary = "Recusar convite", description = "Atualiza o status do convite para RECUSADO. Exige que o status atual seja PENDENTE.")
    @SecurityRequirements
    @PatchMapping("/recusado/{matchId}")
    public void conviteRecusado(@PathVariable Long matchId) {
        conviteService.conviteRecusado(matchId);
    }

    @Operation(summary = "Cancelar convite", description = "Atualiza o status do convite para CANCELADO. Não permitido se o status for ACEITO.")
    @SecurityRequirements
    @PatchMapping("cancelado/{matchId}")
    public void conviteCancelado(@PathVariable Long matchId) {
        conviteService.conviteCancelado(matchId);
    }
}
