package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p><b>Controller responsável pela entidade {@link Convite}.</b></p>
 * <p>Fornece endpoints para a busca, criação e alteração de {@code Convites}.</p>
 *
 * @author Miguel Lima
 */
@RestController
@RequestMapping("/chat/convite")
@CrossOrigin("*")
public class ConviteController {

    @Autowired
    private ConviteService conviteService;

    /**
     * <p>Busca <b>Convites</b> com base no Identificador do Usuário.</p>
     *
     * @param usuarioId Identificador do Usuário.
     * @param tipoUsuario Tipo de Usuário.
     * @return {@code List<}{@link ConviteResponseDTO}{@code >} Contendo todos os <b>Convites</b> relacionados ao Usuário.
     * Retorna uma lista vazia se não existir <b>Convites</b> relacionados ao Usuário.
     */
    @GetMapping("/listarPorUsuario/{usuarioId}/{tipoUsuario}")
    private List<ConviteResponseDTO> listarPorUsuario(@PathVariable Long usuarioId,
                                                      @PathVariable TipoUsuario tipoUsuario) {
        return conviteService.listarPorUsuario(usuarioId, tipoUsuario);
    }

    /**
     * <p>Busca o <b>Convite</b> mais recente utilizando o Identificador de um {@code Match}.</p>
     *
     * @param matchId Identificador de uma entidade <b>Match</b>.
     * @return {@link ConviteResponseDTO} Contendo as informações do <b>Convite</b>.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteNaoEncontradoUsandoReferencia Se não for
     * encontrado um <b>Convite</b> relacionado a referência.
     */
    @GetMapping("/buscarConviteRecente/{matchId}")
    private ConviteResponseDTO buscarConviteRecente(@PathVariable Long matchId) {
        return conviteService.buscarConviteRecente(matchId);
    }

    /**
     * <p>Adiciona um novo {@code Convite} ao Sistema.</p>
     * @param matchId Identificador de uma entidade <b>Match</b>.
     * @param dto DTO que traz informações do novo <b>Convite</b>.
     * @return {@link ConviteResponseDTO} Contendo as informações do <b>Convite</b>.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteExistente Caso exista outro
     * <b>Convite</b> no Sistema que possua status {@code PENDENTE}<b>/</b>{@code ACEITO}.
     */
    @PostMapping("/enviar/{matchId}")
    private ConviteResponseDTO enviarConvite(@PathVariable Long matchId, @RequestBody ConviteRequestDTO dto) {
        return conviteService.novoConvite(ConviteStatus.PENDENTE, dto, matchId);
    }

    /**
     * <p>Atualiza o status de um <b>Convite</b> para {@code ACEITO}.</p>
     *
     * @param matchId Identificador de uma entidade <b>Match</b>.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteNaoEncontradoUsandoReferencia Se não for
     * encontrado um <b>Convite</b> relacionado a referência.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatusException Se o status do
     * <b>Convite</b> for diferente de {@code PENDENTE}.
     */
    @PatchMapping("/aceito/{matchId}")
    private void  conviteAceito(@PathVariable Long matchId) {
        conviteService.conviteAceito(matchId);
    }

    /**
     * <p>Atualiza o status de um <b>Convite</b> para {@code RECUSADO}.</p>
     *
     * @param matchId Identificador de uma entidade <b>Match</b>.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteNaoEncontradoUsandoReferencia Se não for
     * encontrado um <b>Convite</b> relacionado a referência.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatusException Se o status do
     * <b>Convite</b> for diferente de {@code PENDENTE}.
     */
    @PatchMapping("/recusado/{matchId}")
    private void conviteRecusado(@PathVariable Long matchId) {
        conviteService.conviteRecusado(matchId);
    }

    /**
     * <p>Atualiza o status de um <b>Convite</b> para {@code CANCELADO}.</p>
     *
     * @param matchId Identificador de uma entidade <b>Match</b>.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteNaoEncontradoUsandoReferencia Se não for
     * encontrado um <b>Convite</b> relacionado a referência.
     * @throws com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatusException Se o status do
     * <b>Convite</b> for {@code ACEITO}.
     */
    @PatchMapping("cancelado/{matchId}")
    private void conviteCancelado(@PathVariable Long matchId) {
        conviteService.conviteCancelado(matchId);
    }
}