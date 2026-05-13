package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PrefereciasAnfitriaoDTO;
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
    public PreferenciasAnfitriao criarPreferencia(@PathVariable Long userId, @RequestBody PrefereciasAnfitriaoDTO prefereciasAnfitriaoDTO) {
        return pas.criarPreferencia(userId, prefereciasAnfitriaoDTO);
    }

    @PutMapping("/editar/{id}")
    public PreferenciasAnfitriao editarPreferencias(@PathVariable Long id, @RequestBody PrefereciasAnfitriaoDTO preferenciasDTO) {
        return pas.editarPreferencias(id, preferenciasDTO);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        pas.excluir(id);
    }

}
