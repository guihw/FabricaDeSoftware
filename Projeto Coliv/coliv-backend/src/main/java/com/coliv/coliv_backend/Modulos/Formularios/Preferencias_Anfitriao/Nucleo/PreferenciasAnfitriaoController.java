package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciasAnfitriaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Preferências do Anfitrião", description = "Preferências de perfil de colega que o anfitrião deseja receber. Requer role ANFITRIÃO.")
@RestController
@RequestMapping("/formularios/preferencias-anfitriao")
@CrossOrigin(origins = "*")
class PreferenciasAnfitriaoController {

    @Autowired
    private PreferenciasAnfitriaoService pas;

    @Operation(summary = "Listar todas as preferências de anfitriões")
    @GetMapping("/listar")
    public List<PreferenciasAnfitriao> listar() {
        return pas.listar();
    }

    @Operation(summary = "Buscar preferências por ID")
    @GetMapping("/buscar/{id}")
    public PreferenciasAnfitriao buscarPorId(@PathVariable Long id) {
        return pas.buscarPorId(id);
    }

    @Operation(summary = "Criar preferências para anfitrião")
    @PostMapping("{userId}/nova-preferencia")
    public PreferenciasAnfitriaoDTO criarPreferencia(@PathVariable Long userId,
                                                     @RequestBody PreferenciasAnfitriaoDTO preferenciasAnfitriaoDTO) {
        return pas.criarPreferencia(userId, preferenciasAnfitriaoDTO);
    }

    @Operation(summary = "Editar preferências do anfitrião")
    @PutMapping("/editar/{anfitriaoId}")
    public PreferenciasAnfitriaoDTO editarPreferencias(@PathVariable Long anfitriaoId,
                                                       @RequestBody PreferenciasAnfitriaoDTO preferenciasDTO) {
        return pas.editarPreferencias(anfitriaoId, preferenciasDTO);
    }

    @Operation(summary = "Excluir preferências do anfitrião")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        pas.excluir(id);
    }
}
