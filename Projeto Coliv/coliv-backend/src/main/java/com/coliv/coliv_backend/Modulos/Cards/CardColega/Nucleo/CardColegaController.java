package com.coliv.coliv_backend.Modulos.Cards.CardColega.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos.CardColegaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Card do Colega", description = "Card público do colega exibido no feed do anfitrião")
@RestController
@RequestMapping("/cards/colega")
@CrossOrigin("*")
class CardColegaController {

    @Autowired
    private CardColegaService service;

    @Operation(summary = "Listar todos os cards de colegas (entidade bruta)")
    @GetMapping("/listar")
    public List<CardColega> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar card por ID (entidade bruta)")
    @GetMapping("/buscar/{id}")
    public CardColega buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Obter card completo do colega", description = "Retorna dados compostos: usuário + preferências + card")
    @GetMapping("/card/info/{colegaId}")
    public CardColegaResponseDTO cardInfo(@PathVariable Long colegaId) {
        return service.getCardCompleteInfo(colegaId);
    }

    @Operation(summary = "Excluir card do colega")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
