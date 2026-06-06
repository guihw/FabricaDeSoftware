package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.ComentarioDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@CrossOrigin("*")
class ComentarioController {

    private final ComentarioService service;

    ComentarioController(ComentarioService service) {
        this.service = service;
    }

    @PostMapping("/criar")
    public Comentario criar(
            @RequestBody ComentarioDTO dto
    ) {
        return service.criar(dto);
    }

    @GetMapping("/listar")
    public List<Comentario> listar() {
        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public Comentario buscarPorId(
            @PathVariable Long id
    ) {
        return service.buscarPorId(id);
    }

    @PutMapping("/editar/{id}")
    public Comentario editar(
            @PathVariable Long id,
            @RequestBody ComentarioDTO dto
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