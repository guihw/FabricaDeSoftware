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

    @GetMapping("/buscarConviteRecente/{chatId}")
    private ConviteResponseDTO buscarConviteRecente(@PathVariable Long chatId) {
        return conviteService.buscarConviteRecente(chatId);
    }

    @PostMapping("/enviar/{chatId}")
    private ConviteResponseDTO enviarConvite(@PathVariable Long chatId, @RequestBody ConviteRequestDTO dto) {
        return conviteService.novoConvite(ConviteStatus.PENDENTE, dto, chatId);
    }

    @PatchMapping("/aceito/{chatId}")
    private void  conviteAceito(@PathVariable Long chatId) {
        conviteService.conviteAceito(chatId);
    }

    @PatchMapping("/recusado/{chatId}")
    private void conviteRecusado(@PathVariable Long chatId) {
        conviteService.conviteRecusado(chatId);
    }

    @PatchMapping("cancelado/{chatId}")
    private void conviteCancelado(@PathVariable Long chatId) {
        conviteService.conviteCancelado(chatId);
    }
}