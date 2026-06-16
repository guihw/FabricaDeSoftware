package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemNaoEncontradaUsandoReferencias;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <b>Controller responsável pela entidade {@link MensagemDireta}</b>
 * <p>
 *     Fornece endpoints para a requisição, criação, alteração e remoção de Mensagens no Sistema (CRUD)
 * </p>
 * <br><br>
 * @author Miguel Lima
 */
@RestController
@RequestMapping("/chat/mensagem")
@CrossOrigin ("*")
class MensagemDiretaController {

    @Autowired
    private MensagemDiretaService msgService;

    /**
     * <p>Busca todas as entidades {@link MensagemDireta} existentes no Sistema</p>
     * <br><br>
     * @return Retorna uma lista de {@link MensagemResponseDTO}.<br>
     *         Se nenhuma {@code Mensagem} for encontrada, retorna uma lista vazia.
     */
    @GetMapping("/listar")
    public List<MensagemResponseDTO> listar() {
        return msgService.listar();
    }

    /**
     * <p>
     *     Busca todas as entidades {@link MensagemDireta} existentes no Sistema utilizando como referência o Chat Id.
     * </p>
     * <br><br>
     * @param chatId Identificador do Chat a qual a {@code Mensagem} pertence (Path Variable).
     * @return {@code List<}{@link MensagemResponseDTO}{@code >} contendo informações sobre as Mensagens encontradas.
     * <br>Se nenhuma {@code Mensagem} for encontrada usando a referência {@code chatId}, retorna uma lista vazia.
     */
    @GetMapping("/buscarPorChatId/{chatId}")
    public List<MensagemResponseDTO> listarPorChat(@PathVariable Long chatId) {
        return msgService.buscarPorChatId(chatId);
    }

    /**
     * <p>
     *      Busca todas as entidades {@link MensagemDireta} existentes no Sistema utilizando como referência, Usuario Id
     *      e o tipo do usuário.
     * </p>
     * <br><br>
     * @param usuarioId Identificador do Usuário a qual a {@code Mensagem} pertence (Path Variable).
     * @param tipoUsuario Tipo referente ao Usuário (Path Variable).
     * @return {@code List<}{@link MensagemResponseDTO}{@code >} contendo informações sobre as Mensagens encontradas.
     * <br>Se nenhuma {@code Mensagem} for encontrada usando as referências, {@code usuarioId} e {@code tipoUsuario},
     * retorna uma lista vazia.
     */
    @GetMapping("/buscarPorUsuarioId/{usuarioId}/{tipoUsuario}")
    public List<MensagemResponseDTO> listarPorUsuarioId(@PathVariable Long usuarioId,
                                                        @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorUsuarioId(usuarioId, tipoUsuario);
    }

    /**
     * <p>
     *      Busca todas as entidades {@link MensagemDireta} existentes no Sistema utilizando como referência, Usuario Id,
     *      Chat Id e o tipo do usuário.
     * </p>
     * <br><br>
     * @param usuarioId Identificador do Usuário a qual a {@code Mensagem} pertence (Path Variable).
     * @param chatId Identificador do Chat a qual a {@code Mensagem} pertence (Path Variable).
     * @param tipoUsuario Tipo referente ao Usuário (Path Variable).
     * @return {@code List<}{@link MensagemResponseDTO}{@code >} contendo informações sobre as Mensagens encontradas.
     * <br>Se nenhuma {@code Mensagem} for encontrada usando as referências, retorna uma lista vazia.
     */
    @GetMapping("/buscarPorChatEUsuario/{usuarioId}/{chatId}/{tipoUsuario}")
    public List<MensagemResponseDTO> buscarPorChatEUsuario(@PathVariable Long usuarioId,@PathVariable Long chatId,
                                                        @PathVariable TipoUsuario tipoUsuario) {
        return msgService.buscarPorChatEUsuario(chatId, usuarioId, tipoUsuario);
    }

    /**
     * <p>
     *     Busca todas as entidades {@link MensagemDireta} existentes no Sistema utilizando como referência, Chat Id e
     *     conteúdo.
     * </p>
     * <br><br>
     * @param chatId Identificador do Chat a qual a {@code Mensagem} pertence (Path Variable).
     * @param texto Texto utilizado como alvo na busca (Path Variable).
     * @return {@code List<}{@link MensagemResponseDTO}{@code >} contendo todas as {@code Mensagens} que possuam o
     * {@code texto} em um Chat.
     * <br>Se nenhuma {@code Mensagem} for encontrada, retorna uma lista vazia.
     */
    @GetMapping("/buscarPorChatETexto/{chatId}/{texto}")
    public List<MensagemResponseDTO> buscarPorChatETexto(@PathVariable Long chatId, @PathVariable String texto) {
        return msgService.buscarPorChatETexto(chatId, texto);
    }

    /**
     * <p>
     *     Adiciona uma nova {@link MensagemDireta} ao Sistema.
     * </p>
     * <br><br>
     * @param usuarioId Identificador do Usuário a qual a {@code Mensagem} pertence (Path Variable).
     * @param chatId Identificador do Chat a qual a {@code Mensagem} pertence (Path Variable).
     * @param dto Uma DTO do tipo {@link MensagemRequestDTO}, que contém as informações da nova {@code Mensagem}
     *            (Request Body).
     * @return Retorna uma {@link MensagemResponseDTO} com as informações da nova {@code Mensagem}.
     */
    @PostMapping("/{usuarioId}/{chatId}/nova")
    public MensagemResponseDTO criarMensagem(@PathVariable Long usuarioId, @PathVariable Long chatId,
                                             @RequestBody MensagemRequestDTO dto) {
        return msgService.criarMensagem(usuarioId, chatId, dto);
    }

    /**
     * <p>
     *     Atualiza uma {@link MensagemDireta} existente no Sistema.
     * </p>
     * <br><br>
     * @param sequencialId Identificador da {@code Mensagem} existente em um {@link Chat} (Path Variable).
     * @param usuarioId Identificador do {@code Usuario} que criou a {@code Mensagem} (Path Variable).
     * @param chatId Identificador do {@code Chat} a qual a {@code Mensagem} pertence (Path Variable).
     * @param dto DTO do tipo {@link MensagemRequestDTO}, contendo as informações a serem atualizadas (Request Body).
     * @return Retorna uma DTO tipo {@link MensagemResponseDTO}, com as informações da {@code Mensagem} atualizada.
     * @throws MensagemNaoEncontradaUsandoReferencias Caso não encontre nenhuma {@code Mensagem} utilizando as
     * referências fornecidas.
     */
    @PutMapping("/editar/{sequencialId}/{chatId}/{usuarioId}")
    public MensagemResponseDTO editarMensagem(@PathVariable Long sequencialId, @PathVariable Long usuarioId,
                                              @PathVariable Long chatId, @RequestBody MensagemRequestDTO dto) {
        return msgService.editarMensagem(sequencialId, chatId, usuarioId, dto);
    }

    /**
     * <p>
     *     Excluí uma {@link MensagemDireta} existente no Sistema.
     * </p>
     * <br><br>
     * @param sequencialId Identificador da {@code Mensagem} existente em um {@link Chat} (Path Variable).
     * @param usuarioId Identificador do {@code Usuario} que criou a {@code Mensagem} (Path Variable).
     * @param chatId Identificador do {@code Chat} a qual a {@code Mensagem} pertence (Path Variable).
     * @throws MensagemNaoEncontradaUsandoReferencias Caso não encontre nenhuma {@code Mensagem} utilizando as
     * referências fornecidas.
     */
    @DeleteMapping("/excluir/{sequencialId}/{chatId}/{usuarioId}")
    public void excluirMensagem (@PathVariable Long sequencialId, @PathVariable Long usuarioId,
                                 @PathVariable Long chatId) {
        msgService.excluirMensagem(sequencialId, chatId, usuarioId);
    }
}