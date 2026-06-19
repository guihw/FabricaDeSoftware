package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/convite")
@CrossOrigin("*")
public class ConviteController {

    @Autowired
    private ConviteService conviteService;

    @GetMapping("/listarPorUsuario/{usuarioId}/{tipoUsuario}")
    private List<ConviteResponseDTO> listarPorUsuario(@PathVariable Long usuarioId,
                                                      @PathVariable TipoUsuario tipoUsuario) {
        return conviteService.listarPorUsuario(usuarioId, tipoUsuario);
    }

    @GetMapping("/buscarConviteRecente/{matchId}")
    private ConviteResponseDTO buscarConviteRecente(@PathVariable Long matchId) {
        return conviteService.buscarConviteRecente(matchId);
    }

    @PostMapping("/enviar/{matchId}")
    private ConviteResponseDTO enviarConvite(@PathVariable Long matchId, @RequestBody ConviteRequestDTO dto) {
        return conviteService.novoConvite(ConviteStatus.PENDENTE, dto, matchId);
    }

    @PatchMapping("/aceito/{matchId}")
    private void  conviteAceito(@PathVariable Long matchId) {
        conviteService.conviteAceito(matchId);
    }

    @PatchMapping("/recusado/{matchId}")
    private void conviteRecusado(@PathVariable Long matchId) {
        conviteService.conviteRecusado(matchId);
    }

    @PatchMapping("cancelado/{matchId}")
    private void conviteCancelado(@PathVariable Long matchId) {
        conviteService.conviteCancelado(matchId);
    }
}