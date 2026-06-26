package com.coliv.coliv_backend.Modulos.Recomendacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.FeedPageDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoCardAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoColegaDTO;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recomendações", description = "Feed paginado de recomendações geradas pelo algoritmo de matching. Colega vê imóveis; anfitrião vê colegas.")
@RestController
@RequestMapping("/recomendacoes")
@CrossOrigin("*")
public class RecomendacaoController {

    @Autowired
    private RecomendacaoService recomendacaoService;

    @Operation(
        summary = "Feed do colega",
        description = "Retorna página de imóveis recomendados para o colega. Requer role COLEGA. " +
                      "O colegaId no path é opcional — se omitido, usa o ID do token JWT."
    )
    @GetMapping("/feed/colega/{colegaId}")
    public FeedPageDTO<RecomendacaoCardAnfitriaoDTO> feedColega(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable(required = false) Long colegaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        Long id = colegaId != null ? colegaId : usuario.getId();
        return recomendacaoService.feedColega(id, pagina, tamanho);
    }

    @Operation(
        summary = "Feed do anfitrião",
        description = "Retorna página de colegas recomendados para o anfitrião. Requer role ANFITRIÃO."
    )
    @GetMapping("/feed/anfitriao/{anfitriaoId}")
    public FeedPageDTO<RecomendacaoColegaDTO> feedAnfitriao(
            @PathVariable Long anfitriaoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return recomendacaoService.feedAnfitriao(anfitriaoId, pagina, tamanho);
    }
}
