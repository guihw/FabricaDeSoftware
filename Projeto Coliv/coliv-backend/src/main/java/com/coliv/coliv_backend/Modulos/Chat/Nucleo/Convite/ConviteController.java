package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteInfoDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.ConviteStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/convite")
@CrossOrigin("*")
public class ConviteController {

    @Autowired
    private ConviteService conviteService;

    @PostMapping("/novo/{chatId}")
    private ConviteInfoDTO enviarConvite(@PathVariable Long chatId, @RequestBody ConviteRequestDTO dto) {
        return conviteService.novoConvite(ConviteStatus.PENDENTE, dto, chatId);
    }

    @PatchMapping("/aceito/{chatId}")
    private void  conviteAceito(@PathVariable Long chatId) {
        conviteService.conviteAceito(chatId);
    }

    @PatchMapping("/negado/{chatId}")
    private void conviteNegado(@PathVariable Long chatId) {
        conviteService.conviteNegado(chatId);
    }
}