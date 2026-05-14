package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios/colega")
@CrossOrigin(origins = "*")
class ColegaController {

    @Autowired
    private ColegaService colegaService;

    @GetMapping("/listar")
    public List<Colega> listar() {
        return colegaService.getAllColegas();
    }

    @GetMapping("/buscar/{id}")
    public Colega buscarPorId (@PathVariable Long id) {
        return colegaService.getColega(id);
    }

    @PostMapping("/novo")
    public ColegaResponse criarColega(@RequestBody CreateColegaRequest colegaRequest) {
        return colegaService.createColega(colegaRequest);
    }

    @PutMapping("/editar/{id}")
    public Colega editarColega(@PathVariable Long id, @RequestBody Colega colega) {
        return colegaService.editarColega(id, colega);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        colegaService.excluir(id);
    }
}
