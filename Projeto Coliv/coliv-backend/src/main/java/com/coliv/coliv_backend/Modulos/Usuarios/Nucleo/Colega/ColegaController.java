package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios/colega")
@CrossOrigin(origins = "*")
public class ColegaController {

    private final ColegaService colegaService;

    public ColegaController(ColegaService colegaService) {
        this.colegaService = colegaService;
    }

    @GetMapping("/listar")
    public List<ColegaResponse> listar() {
        return colegaService.listar();
    }

    @GetMapping("/buscar/{id}")
    public ColegaResponse buscarPorId(@PathVariable Long id) {
        return colegaService.getColega(id);
    }

    @PostMapping("/novo")
    public ColegaResponse criarColega(
            @Valid @RequestBody CreateColegaRequest colegaRequest
    ) {

        return colegaService.createColega(colegaRequest);
    }

    @PutMapping("/editar/{id}")
    public ColegaResponse editarColega(
            @PathVariable Long id,
            @RequestBody UpdateColegaRequest request
    ) {

        return colegaService.editarColega(id, request);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        colegaService.excluir(id);
    }
}