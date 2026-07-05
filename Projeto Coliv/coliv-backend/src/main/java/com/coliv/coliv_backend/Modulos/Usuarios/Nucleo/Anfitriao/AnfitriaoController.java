package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoPostDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoPutDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Anfitriões", description = "Cadastro e gerenciamento de usuários do tipo Anfitrião")
@RestController
@RequestMapping("/usuarios/anfitriao")
@CrossOrigin(origins = "*")
class AnfitriaoController {

    @Autowired
    private AnfitriaoService anfitriaoService;

    @Operation(summary = "Listar anfitriões", description = "Retorna todos os anfitriões cadastrados")
    @GetMapping("/listar")
    public List<UsuarioDTO> listar() {
        return anfitriaoService.listar();
    }

    @Operation(summary = "Buscar anfitrião por ID")
    @GetMapping("/buscar/{id}")
    public UsuarioDTO buscarPorId(@PathVariable Long id) {
        return anfitriaoService.buscarPorId(id);
    }

    @Operation(summary = "Buscar anfitriões por lista de IDs", description = "Retorna vários anfitriões em uma única consulta")
    @GetMapping("/buscarPorIds")
    public List<UsuarioDTO> buscarPorIds(@RequestParam List<Long> ids) {
        return anfitriaoService.obterUsuarios(ids);
    }

    @Operation(summary = "Cadastrar anfitrião", description = "Cria um novo anfitrião. Endpoint público — não requer token.")
    @SecurityRequirements
    @PostMapping("/novo")
    public AnfitriaoPostDTO criarAnfitriao(@RequestBody AnfitriaoPostDTO anfitriaoPostDTO) {
        return anfitriaoService.criarAnfitriao(anfitriaoPostDTO);
    }

    @Operation(summary = "Editar anfitrião")
    @PutMapping("/editar/{id}")
    public AnfitriaoPutDTO editarAnfitriao(@PathVariable Long id, @RequestBody AnfitriaoPostDTO anfitriao) {
        return anfitriaoService.editarAnfitriao(id, anfitriao);
    }

    @Operation(summary = "Excluir anfitrião")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        anfitriaoService.excluir(id);
    }
}
