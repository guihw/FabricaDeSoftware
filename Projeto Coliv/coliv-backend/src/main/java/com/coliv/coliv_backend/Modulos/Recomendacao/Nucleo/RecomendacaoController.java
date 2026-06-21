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

    @GetMapping("/feed/colega")
    public FeedPageDTO<RecomendacaoCardAnfitriaoDTO> feedColega(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return recomendacaoService.feedColega(usuario.getId(), pagina, tamanho);
    }

    @GetMapping("/feed/colega/{colegaId}")
    public FeedPageDTO<RecomendacaoCardAnfitriaoDTO> feedColega(
            @PathVariable Long colegaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return recomendacaoService.feedColega(colegaId, pagina, tamanho);
    }

    @GetMapping("/feed/anfitriao/{anfitriaoId}")
    public FeedPageDTO<RecomendacaoColegaDTO> feedAnfitriao(
            @PathVariable Long anfitriaoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return recomendacaoService.feedAnfitriao(anfitriaoId, pagina, tamanho);
    }
}
