package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formularios/preferencias-colega")
@CrossOrigin(origins = "*")
public class PreferenciasColegaController {

    private final PreferenciasColegaService service;

    public PreferenciasColegaController(PreferenciasColegaService service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public List<PreferenciasColegaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public PreferenciasColegaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping("/{colegaId}/nova-preferencia")
    public PreferenciasColegaResponse criarPreferencia(
            @PathVariable Long colegaId,
            @Valid @RequestBody PreferenciasColegaDTO dto
    ) {

        return service.criarPreferencia(colegaId, dto);
    }

    @PutMapping("/editar/{id}")
    public PreferenciasColegaResponse editarPreferencias(
            @PathVariable Long id,
            @Valid @RequestBody PreferenciasColegaDTO dto
    ) {

        return service.editarPreferencias(id, dto);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}