package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoRequestDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/card/info/{anfitriaoId}")
    public CardAnfitriaoResponseDTO cardInfo(@PathVariable Long anfitriaoId) {
        return cas.getCardCompleteInfo(anfitriaoId);
    }

    @GetMapping("/card/info/listar")
    public List<CardAnfitriaoResponseDTO> cardInfoList() {
        return cas.getCardCompleteInfoList();
    }

    @PostMapping("/{anfitriaoId}/novo-card-anfitriao")
    public CardAnfitriaoRequestDTO criarCardAnfitriao(@PathVariable Long anfitriaoId, @RequestBody CardAnfitriaoRequestDTO dto) {
        return cas.criarCardAnfitriao(anfitriaoId, dto);
    }

    @PutMapping("/editar/{anfitriaoId}")
    public CardAnfitriaoRequestDTO editarCardAnfitriao(@PathVariable Long anfitriaoId, @RequestBody CardAnfitriaoRequestDTO dto) {
        return cas.editarCardAnfitriao(anfitriaoId, dto);
    }

    @PutMapping("/{anfitriaoId}/arquivos")
    public ResponseEntity<Void> atualizarArquivos(
            @PathVariable Long anfitriaoId,
            @RequestBody List<Long> arquivos) {
        cas.atualizarArquivos(anfitriaoId, arquivos);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        cas.excluir(id);
    }
}