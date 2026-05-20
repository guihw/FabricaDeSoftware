package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formularios/preferencias-colega")
@CrossOrigin(origins = "*")
public class PreferenciasColegaController {

    @Autowired
    private PreferenciasColegaService service;

    @GetMapping("/listar")
    public List<PreferenciasColega> listar() {
        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public PreferenciasColega buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping("/{colegaId}/nova-preferencia")
    public PreferenciasColega criarPreferencia(
            @PathVariable Long colegaId,
            @RequestBody PreferenciasColegaDTO dto
    ) {

        return service.criarPreferencia(colegaId, dto);
    }

    @PutMapping("/editar/{id}")
    public PreferenciasColega editarPreferencias(
            @PathVariable Long id,
            @RequestBody PreferenciasColegaDTO dto
    ) {

        return service.editarPreferencias(id, dto);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}