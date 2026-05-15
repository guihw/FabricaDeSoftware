package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{anfitriaoId}/novo-dados-imovel")
    public DadosImovel criarDadosImovel (@PathVariable Long anfitriaoId, @RequestBody DadosImovelDTO dto) {
        return dis.criarDadosImovel(anfitriaoId, dto);
    }

    @PutMapping("/editar/{anfitriaoId}")
    public DadosImovel editarDadosImovel (@PathVariable Long anfitriaoId, @RequestBody DadosImovelDTO dto) {
        return dis.editarDadosImovel(anfitriaoId, dto);
    }

    @DeleteMapping("excluir/{id}")
    public void excluir (@PathVariable Long id) {
        dis.excluir(id);
    }
}