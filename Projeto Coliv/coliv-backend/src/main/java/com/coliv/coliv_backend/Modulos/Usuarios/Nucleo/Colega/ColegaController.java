package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Colegas", description = "Cadastro e gerenciamento de usuários do tipo Colega (inquilino)")
@RestController
@RequestMapping("/usuarios/colega")
@CrossOrigin(origins = "*")
public class ColegaController {

    private final ColegaService colegaService;

    public ColegaController(ColegaService colegaService) {
        this.colegaService = colegaService;
    }

    @Operation(summary = "Listar colegas", description = "Retorna todos os colegas cadastrados")
    @GetMapping("/listar")
    public List<ColegaResponse> listar() {
        return colegaService.listar();
    }

    @Operation(summary = "Buscar colega por ID")
    @GetMapping("/buscar/{id}")
    public ColegaResponse buscarPorId(@PathVariable Long id) {
        return colegaService.getColega(id);
    }

    @Operation(summary = "Buscar colegas por lista de IDs", description = "Retorna vários colegas em uma única consulta")
    @GetMapping("/buscarPorIds")
    public List<ColegaResponse> buscarPorIds(@RequestParam List<Long> ids) {
        return colegaService.getColegas(ids);
    }

    @Operation(summary = "Cadastrar colega", description = "Cria um novo colega. Endpoint público — não requer token.")
    @SecurityRequirements
    @PostMapping("/novo")
    public ColegaResponse criarColega(@Valid @RequestBody CreateColegaRequest colegaRequest) {
        return colegaService.createColega(colegaRequest);
    }

    @Operation(summary = "Editar colega")
    @PutMapping("/editar/{id}")
    public ColegaResponse editarColega(@PathVariable Long id, @RequestBody UpdateColegaRequest request) {
        return colegaService.editarColega(id, request);
    }

    @Operation(summary = "Excluir colega")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        colegaService.excluir(id);
    }
}
