package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoPostDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoPutDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
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
    public List<UsuarioDTO> listar() {
        return anfitriaoService.listar();
    }

    @GetMapping("/buscar/{id}")
    public UsuarioDTO buscarPorId (@PathVariable Long id) {
        return anfitriaoService.buscarPorId(id);
    }

    @PostMapping("/novo")
    public AnfitriaoPostDTO criarAnfitriao(@RequestBody AnfitriaoPostDTO anfitriaoPostDTO) {
        return anfitriaoService.criarAnfitriao(anfitriaoPostDTO);
    }

    @PutMapping("/editar/{id}")
    public AnfitriaoPutDTO editarAnfitriao(@PathVariable Long id, @RequestBody AnfitriaoPostDTO anfitriao) {
        return anfitriaoService.editarAnfitriao(id, anfitriao);
    }

    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        anfitriaoService.excluir(id);
    }
}
