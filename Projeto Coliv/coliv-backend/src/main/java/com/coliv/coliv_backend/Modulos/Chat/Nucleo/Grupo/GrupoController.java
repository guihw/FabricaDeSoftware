package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <b>Controller responsável pela comunicação entre Frontend e o Backend da entidade {@link Grupo}</b>
 *
 * <p>Fornece endpoints para a requisição e alteração de Grupos presentes no Sistema.</p>
 * <br><br>
 *
 * @author Miguel Lima
 */
@RestController
@RequestMapping("/chat/grupo")
@CrossOrigin("*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    /**
     * <p>
     *     Busca todos os {@link Grupo}s existentes no Sistema.
     * </p>
     * <br><br>
     *     @return Retorna uma lista de {@link GrupoResponseDTO}.<br>
     *     Se nenhum {@code Grupo} for encontrado, retorna uma lista vazia.
     *
     */
    @GetMapping("/listar")
    private List<GrupoResponseDTO> listar() {
        return grupoService.listar();
    }

    /**
     * <p>
     *      Busca um Grupo utilizando ID e Tipo de Usuário.
     * </p>
     * <br><br>
     * @param usuarioId O identificador do Usuário (Path Variable).
     * @param tipoUsuario O tipo do Usuário (Path Variable).
     * <br><br>
     * @return {@link GrupoResponseDTO} com informações do {@code Grupo} encontrado.
     * @throws GrupoNaoEncontradoUsandoReferencia caso não encontre um {@code Grupo} utilizando {@code  usuarioId}.
     */
    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    private GrupoResponseDTO buscarPorUsuarioId(@PathVariable Long usuarioId, @PathVariable TipoUsuario tipoUsuario) {
        return grupoService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    /**
     * <p>
     *     Atualiza o nome de um {@link Grupo}
     * </p>
     * <br><br>
     * @param id O identificador do {@code Grupo} (Path Variable).
     * @param data {@code Map} contento o novo nome do grupo. {@code {"nome": "string"}}.
     * <br><br>
     * @return {@link GrupoResponseDTO} com informações do {@code Grupo} atualizado
     *
     * @throws GrupoIDNaoEncontrado se nenhum {@code Grupo} for encontrado utilizando {@code id}
     */
    @PatchMapping("/editar/nome/{id}")
    private GrupoResponseDTO editarNome(@PathVariable Long id, @RequestBody Map<String, String> data) {
        String nome =  data.get("nome");

        return grupoService.editarNome(id, nome);
    }
}