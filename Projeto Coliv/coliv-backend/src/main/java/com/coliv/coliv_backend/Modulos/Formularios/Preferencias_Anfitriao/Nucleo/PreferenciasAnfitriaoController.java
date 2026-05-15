package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciasAnfitriaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/formularios/preferencias-anfitriao")
@CrossOrigin(origins = "*")
class PreferenciasAnfitriaoController {

    @Autowired
    private PreferenciasAnfitriaoService pas;

    @GetMapping("/listar")
    public List<PreferenciasAnfitriao> listar() {
        return pas.listar();
    }

    @GetMapping("/buscar/{id}")
    public PreferenciasAnfitriao buscarPorId(@PathVariable Long id) {
        return pas.buscarPorId(id);
    }

    @PostMapping("{userId}/nova-preferencia")
    public PreferenciasAnfitriao criarPreferencia(@PathVariable Long userId, @RequestBody PreferenciasAnfitriaoDTO preferenciasAnfitriaoDTO) {
        return pas.criarPreferencia(userId, preferenciasAnfitriaoDTO);
    }

    @PutMapping("/editar/{anfitriaoId}")
    public PreferenciasAnfitriao editarPreferencias(@PathVariable Long anfitriaoId, @RequestBody PreferenciasAnfitriaoDTO preferenciasDTO) {
        return pas.editarPreferencias(anfitriaoId, preferenciasDTO);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        pas.excluir(id);
    }

}
