package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards/anfitriao")
@CrossOrigin("*")
class CardAnfitriaoController {

    @Autowired
    private CardAnfitriaoService cas;

    @GetMapping("/listar")
    public List<CardAnfitriao> listar() {
        return cas.listar();
    }

    @GetMapping("/buscar/{id}")
    public CardAnfitriao buscarPorId(@PathVariable Long id) {
        return cas.buscarPorId(id);
    }

    @PostMapping("/{anfitriaoId}/novo-card-anfitriao")
    public CardAnfitriao criarCardAnfitriao(@PathVariable Long anfitriaoId, @RequestBody CardAnfitriaoDTO dto) {
        return cas.criarCardAnfitriao(anfitriaoId, dto);
    }

    @PutMapping("/editar/{anfitriaoId}")
    public CardAnfitriao editarCardAnfitriao(@PathVariable Long anfitriaoId, @RequestBody CardAnfitriaoDTO dto) {
        return cas.editarCardAnfitriao(anfitriaoId, dto);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        cas.excluir(id);
    }
}