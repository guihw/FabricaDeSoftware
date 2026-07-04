package com.coliv.coliv_backend.Modulos.Notificacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.NotificacaoDTO;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificações", description = "Notificações em tempo real e histórico de não lidas")
@RestController
@RequestMapping("/notificacoes")
@CrossOrigin("*")
class NotificacaoController {

    private final NotificacaoService service;

    NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar notificações não lidas do usuário autenticado")
    @GetMapping
    public List<NotificacaoDTO> listarNaoLidas(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return service.buscarNaoLidas(usuario.getId());
    }

    @Operation(summary = "Marcar uma notificação como lida")
    @PatchMapping("/{id}/lida")
    public void marcarComoLida(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        service.marcarComoLida(id, usuario.getId());
    }
}
