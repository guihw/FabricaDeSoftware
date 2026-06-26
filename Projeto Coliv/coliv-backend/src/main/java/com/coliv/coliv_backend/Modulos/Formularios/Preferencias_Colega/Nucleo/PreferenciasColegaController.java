package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Preferências do Colega", description = "Preferências de moradia do colega (inquilino)")
@RestController
@RequestMapping("/formularios/preferencias-colega")
@CrossOrigin(origins = "*")
public class PreferenciasColegaController {

    private final PreferenciasColegaService service;

    public PreferenciasColegaController(PreferenciasColegaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todas as preferências de colegas")
    @GetMapping("/listar")
    public List<PreferenciasColegaResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar preferências por ID")
    @GetMapping("/buscar/{id}")
    public PreferenciasColegaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar preferências para colega")
    @PostMapping("/{colegaId}/nova-preferencia")
    public PreferenciasColegaResponse criarPreferencia(@PathVariable Long colegaId,
                                                       @Valid @RequestBody PreferenciasColegaDTO dto) {
        return service.criarPreferencia(colegaId, dto);
    }

    @Operation(summary = "Editar preferências do colega")
    @PutMapping("/editar/{id}")
    public PreferenciasColegaResponse editarPreferencias(@PathVariable Long id,
                                                         @Valid @RequestBody PreferenciasColegaDTO dto) {
        return service.editarPreferencias(id, dto);
    }

    @Operation(summary = "Excluir preferências do colega")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
