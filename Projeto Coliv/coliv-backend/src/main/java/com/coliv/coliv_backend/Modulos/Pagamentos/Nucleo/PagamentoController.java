package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.*;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin("*")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @PostMapping("/plus")
    public PixResponse criarPagamento(Authentication authentication) {
        UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
        return service.criarPagamentoPlus(usuario.getId(), usuario.getTipo());
    }

    @GetMapping("/{billingId}/status")
    public StatusPagamentoResponse consultarStatus(
            @PathVariable String billingId,
            Authentication authentication
    ) {
        UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();
        return service.consultarStatus(billingId, usuario.getId());
    }
}
