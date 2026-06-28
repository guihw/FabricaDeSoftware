package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.AvaliacaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Avaliações", description = "Avaliações de anfitriões e colegas após convivência")
@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin("*")
class AvaliacaoController {

    private final AvaliacaoService service;

    AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar avaliação")
    @PostMapping("/criar")
    public Avaliacao criar(@RequestBody AvaliacaoDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Listar avaliações")
    @GetMapping("/listar")
    public List<Avaliacao> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar avaliação por ID")
    @GetMapping("/buscar/{id}")
    public Avaliacao buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Editar avaliação")
    @PutMapping("/editar/{id}")
    public Avaliacao editar(@PathVariable Long id, @RequestBody AvaliacaoDTO dto) {
        return service.editar(id, dto);
    }

    @Operation(summary = "Excluir avaliação")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
