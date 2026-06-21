package com.coliv.coliv_backend.Modulos.Recomendacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.FeedPageDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoCardAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoColegaDTO;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recomendacoes")
@CrossOrigin("*")
public class RecomendacaoController {

    @Autowired
    private RecomendacaoService recomendacaoService;

    @GetMapping("/feed/colega/{colegaId}")
    public FeedPageDTO<RecomendacaoCardAnfitriaoDTO> feedColega(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable(required = false) Long colegaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        Long id = colegaId != null ? colegaId : usuario.getId();

        System.out.println("Usuario autenticado: " + usuario.getUsername());
        System.out.println("Id token: " + usuario.getId());
        System.out.println("Tipo: " + usuario.getTipo());
        System.out.println("Id URL: " + colegaId);
        return recomendacaoService.feedColega(id, pagina, tamanho);
    }

    @GetMapping("/feed/anfitriao/{anfitriaoId}")
    public FeedPageDTO<RecomendacaoColegaDTO> feedAnfitriao(
            @PathVariable Long anfitriaoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return recomendacaoService.feedAnfitriao(anfitriaoId, pagina, tamanho);
    }
}
