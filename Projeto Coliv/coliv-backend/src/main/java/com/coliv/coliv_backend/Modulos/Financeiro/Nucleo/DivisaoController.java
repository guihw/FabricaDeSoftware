package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoDTO;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoResponse;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.IFinanceiro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/divisoes")
@CrossOrigin("*")
class DivisaoController {

    @Autowired
    private DivisaoService service;

     @Autowired
    private IFinanceiro financeiro;

    @PostMapping("/criar")
    public Divisao criar(
            @RequestBody DivisaoDTO dto
    ) {

        return service.criar(dto);
    }

    @GetMapping("/listar")
    public List<Divisao> listar() {

        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public Divisao buscarPorId(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }

    @PutMapping("/editar/{id}")
    public Divisao editar(
            @PathVariable Long id,
            @RequestBody DivisaoDTO dto
    ) {

        return service.editar(id, dto);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(
            @PathVariable Long id
    ) {

        service.excluir(id);
    }

    @GetMapping("/despesa/{despesaId}")
    public List<DivisaoResponse> listarPorDespesa(@PathVariable Long despesaId) {
        return financeiro.getDivisoes(despesaId);
    }
}