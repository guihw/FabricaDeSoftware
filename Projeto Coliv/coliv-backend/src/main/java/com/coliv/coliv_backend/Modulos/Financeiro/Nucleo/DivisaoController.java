package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoDTO;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoResponse;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.IFinanceiro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Divisões", description = "Divisão de despesas entre os moradores")
@RestController
@RequestMapping("/divisoes")
@CrossOrigin("*")
class DivisaoController {

    @Autowired
    private DivisaoService service;

    @Autowired
    private IFinanceiro financeiro;

    @Operation(summary = "Criar divisão")
    @PostMapping("/criar")
    public Divisao criar(@RequestBody DivisaoDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Buscar divisão por ID")
    @GetMapping("/buscar/{id}")
    public Divisao buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Editar divisão")
    @PutMapping("/editar/{id}")
    public Divisao editar(@PathVariable Long id, @RequestBody DivisaoDTO dto) {
        return service.editar(id, dto);
    }

    @Operation(summary = "Excluir divisão")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @Operation(summary = "Listar divisões por despesa", description = "Retorna todas as divisões associadas a uma despesa específica")
    @GetMapping("/despesa/{despesaId}")
    public List<DivisaoResponse> listarPorDespesa(@PathVariable Long despesaId) {
        return financeiro.getDivisoes(despesaId);
    }
}
