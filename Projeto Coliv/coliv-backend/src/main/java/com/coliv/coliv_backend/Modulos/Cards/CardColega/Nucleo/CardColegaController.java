package com.coliv.coliv_backend.Modulos.Cards.CardColega.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos.CardColegaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards/colega")
@CrossOrigin("*")
class CardColegaController {

    @Autowired
    private CardColegaService service;

    @GetMapping("/listar")
    public List<CardColega> listar() {
        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public CardColega buscarPorId(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }

    @GetMapping("/card/info/{colegaId}")
    public CardColegaResponseDTO cardInfo(
            @PathVariable Long colegaId
    ) {

        return service.getCardCompleteInfo(colegaId);
    }
/*
    @GetMapping("/card/info/listar")
    public List<CardColegaResponseDTO> cardInfoList() {

        return service.getCardCompleteInfoList();
    }
*/
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}