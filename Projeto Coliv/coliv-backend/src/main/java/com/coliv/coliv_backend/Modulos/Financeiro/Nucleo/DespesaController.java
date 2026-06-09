package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DespesaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/despesas")
@CrossOrigin("*")
class DespesaController {

    @Autowired
    private DespesaService service;

    @PostMapping("/criar")
    public Despesa criar(
            @RequestBody DespesaDTO dto
    ) {

        return service.criar(dto);
    }

    @GetMapping("/listar")
    public List<Despesa> listar() {

        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public Despesa buscarPorId(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }

    @PutMapping("/editar/{id}")
    public Despesa editar(
            @PathVariable Long id,
            @RequestBody DespesaDTO dto
    ) {

        return service.editar(id, dto);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(
            @PathVariable Long id
    ) {

        service.excluir(id);
    }
}