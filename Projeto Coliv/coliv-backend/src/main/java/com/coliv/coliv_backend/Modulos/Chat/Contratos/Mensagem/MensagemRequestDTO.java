package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta.MensagemDireta;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

/**
 * <p><b>DTO que recebe informações para criação e atualização de {@link MensagemDireta}</b></p>
 * <br><br>
 * @see TipoUsuario
 *
 * @param texto Texto a ser adicionado a Mensagem.
 * @param tipoUsuario Tipo do Usuário criador da Mensagem.
 * @param arquivoId Identificador do Arquivo que pode ser adicionado a Mensagem (Opcional).
 */
public record MensagemRequestDTO(String texto, TipoUsuario tipoUsuario, Long arquivoId) {
}