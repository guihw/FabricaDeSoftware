package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Grupos", description = "Grupos de chat entre moradores. Endpoints públicos.")
@RestController
@RequestMapping("/chat/grupo")
@CrossOrigin("*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Operation(summary = "Listar todos os grupos")
    @SecurityRequirements
    @GetMapping("/listar")
    public List<GrupoResponseDTO> listar() {
        return grupoService.listar();
    }

    @Operation(summary = "Buscar grupo por usuário", description = "Retorna o grupo do usuário identificado por ID e tipo")
    @SecurityRequirements
    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    public GrupoResponseDTO buscarPorUsuarioId(@PathVariable Long usuarioId,
                                               @PathVariable TipoUsuario tipoUsuario) {
        return grupoService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    @Operation(summary = "Editar nome do grupo", description = "Body: { \"nome\": \"novo nome\" }")
    @SecurityRequirements
    @PatchMapping("/editar/nome/{id}")
    public GrupoResponseDTO editarNome(@PathVariable Long id, @RequestBody Map<String, String> data) {
        return grupoService.editarNome(id, data.get("nome"));
    }
}
