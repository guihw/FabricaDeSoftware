package com.coliv.coliv_backend.Modulos.Usuarios.Core.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contracts.Anfitriao.AnfitriaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios/anfitriao")
@CrossOrigin(origins = "*")
class AnfitriaoController {

    @Autowired
    private AnfitriaoService anfitriaoService;

    @GetMapping("/listar")
    public List<Anfitriao> listar() {
        return anfitriaoService.listar();
    }

    @GetMapping("/buscar/{id}")
    public Anfitriao buscarPorId (@PathVariable Long id) {
        return anfitriaoService.buscarPorId(id);
    }

    @PostMapping("/novo")
    public Anfitriao criarAnfitriao(@RequestBody AnfitriaoDTO anfitriaoDTO) {
        return anfitriaoService.criarAnfitriao(anfitriaoDTO);
    }

    @PutMapping("/editar/{id}")
    public Anfitriao editarAnfitriao(@PathVariable Long id, @RequestBody Anfitriao anfitriao) {
        return anfitriaoService.editarAnfitriao(id, anfitriao);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        anfitriaoService.excluir(id);
    }
}
