package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formularios/dados-imovel")
@CrossOrigin("*")
class DadosImovelController {

    @Autowired
    private DadosImovelService dis;

    @GetMapping("/listar")
    public List<DadosImovel> listar() {
        return dis.listar();
    }

    @GetMapping("/buscar/{id}")
    public DadosImovel buscarPorId (@PathVariable Long id) {
        return dis.buscarPorId(id);
    }

    @GetMapping("/anfitriao/{anfitriaoId}")
    public ResponseEntity<DadosImovelDTO> buscarPorAnfitriaoId(@PathVariable Long anfitriaoId) {
        return dis.getDadosImovelSeCompleto(anfitriaoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{anfitriaoId}/novo-dados-imovel")
    public DadosImovelRequestDTO criarDadosImovel (@PathVariable Long anfitriaoId, @RequestBody DadosImovelRequestDTO dto) {
        return dis.criarDadosImovel(anfitriaoId, dto);
    }

    @PreAuthorize("#anfitriaoId == authentication.principal.id")
    @PutMapping("/editar/{anfitriaoId}")
    public DadosImovelRequestDTO editarDadosImovel(@PathVariable Long anfitriaoId,
                                                   @RequestBody DadosImovelRequestDTO dto) {
        return dis.editarDadosImovel(anfitriaoId, dto);
    }

    @DeleteMapping("excluir/{id}")
    public void excluir (@PathVariable Long id) {
        dis.excluir(id);
    }
}