package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.ComentarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comentários", description = "Comentários vinculados a avaliações")
@RestController
@RequestMapping("/comentarios")
@CrossOrigin("*")
class ComentarioController {

    private final ComentarioService service;

    ComentarioController(ComentarioService service) {
        this.service = service;
    }

    @Operation(summary = "Criar comentário")
    @PostMapping("/criar")
    public Comentario criar(@RequestBody ComentarioDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Listar comentários")
    @GetMapping("/listar")
    public List<Comentario> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar comentário por ID")
    @GetMapping("/buscar/{id}")
    public Comentario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Editar comentário")
    @PutMapping("/editar/{id}")
    public Comentario editar(@PathVariable Long id, @RequestBody ComentarioDTO dto) {
        return service.editar(id, dto);
    }

    @Operation(summary = "Excluir comentário")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
