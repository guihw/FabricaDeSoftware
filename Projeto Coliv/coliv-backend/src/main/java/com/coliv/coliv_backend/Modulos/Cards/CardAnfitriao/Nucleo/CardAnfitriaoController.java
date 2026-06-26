package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoRequestDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Card do Anfitrião", description = "Card público do anfitrião exibido no feed do colega. Requer role ANFITRIÃO.")
@RestController
@RequestMapping("/cards/anfitriao")
@CrossOrigin("*")
class CardAnfitriaoController {

    @Autowired
    private CardAnfitriaoService cas;

    @Operation(summary = "Listar todos os cards de anfitriões (entidade bruta)")
    @GetMapping("/listar")
    public List<CardAnfitriao> listar() {
        return cas.listar();
    }

    @Operation(summary = "Buscar card por ID (entidade bruta)")
    @GetMapping("/buscar/{id}")
    public CardAnfitriao buscarPorId(@PathVariable Long id) {
        return cas.buscarPorId(id);
    }

    @Operation(summary = "Obter card completo do anfitrião", description = "Retorna dados compostos: usuário + imóvel + card, incluindo URLs das fotos")
    @GetMapping("/card/info/{anfitriaoId}")
    public CardAnfitriaoResponseDTO cardInfo(@PathVariable Long anfitriaoId) {
        return cas.getCardCompleteInfo(anfitriaoId);
    }

    @Operation(summary = "Listar todos os cards completos de anfitriões")
    @GetMapping("/card/info/listar")
    public List<CardAnfitriaoResponseDTO> cardInfoList() {
        return cas.getCardCompleteInfoList();
    }

    @Operation(summary = "Criar card para anfitrião")
    @PostMapping("/{anfitriaoId}/novo-card-anfitriao")
    public CardAnfitriaoRequestDTO criarCardAnfitriao(@PathVariable Long anfitriaoId,
                                                      @RequestBody CardAnfitriaoRequestDTO dto) {
        return cas.criarCardAnfitriao(anfitriaoId, dto);
    }

    @Operation(summary = "Editar card do anfitrião")
    @PutMapping("/editar/{anfitriaoId}")
    public CardAnfitriaoRequestDTO editarCardAnfitriao(@PathVariable Long anfitriaoId,
                                                       @RequestBody CardAnfitriaoRequestDTO dto) {
        return cas.editarCardAnfitriao(anfitriaoId, dto);
    }

    @Operation(summary = "Atualizar fotos do card", description = "Associa uma lista de IDs de arquivos (já enviados via /arquivos/upload) ao card do anfitrião")
    @PutMapping("/{anfitriaoId}/arquivos")
    public ResponseEntity<Void> atualizarArquivos(@PathVariable Long anfitriaoId,
                                                   @RequestBody List<Long> arquivos) {
        cas.atualizarArquivos(anfitriaoId, arquivos);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir card do anfitrião")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        cas.excluir(id);
    }
}
