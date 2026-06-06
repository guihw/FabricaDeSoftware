package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.AvaliacaoDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin("*")
class AvaliacaoController {

    private final AvaliacaoService service;

    AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping("/criar")
    public Avaliacao criar(
            @RequestBody AvaliacaoDTO dto
    ) {
        return service.criar(dto);
    }

    @GetMapping("/listar")
    public List<Avaliacao> listar() {
        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public Avaliacao buscarPorId(
            @PathVariable Long id
    ) {
        return service.buscarPorId(id);
    }

    @PutMapping("/editar/{id}")
    public Avaliacao editar(
            @PathVariable Long id,
            @RequestBody AvaliacaoDTO dto
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