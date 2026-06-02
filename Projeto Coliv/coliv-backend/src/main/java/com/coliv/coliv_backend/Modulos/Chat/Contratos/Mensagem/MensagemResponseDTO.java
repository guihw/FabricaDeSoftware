package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemMatch.MensagemDireta;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

import java.time.LocalDateTime;

/**
 * <p><b>DTO que carrega informações de uma {@link MensagemDireta}</b></p>
 * <br><br>
 * @see TipoUsuario
 * @param id Identificador da Mensagem. Utilizando {@code sequencialId}.
 * @param texto Texto contido na Mensagem.
 * @param tipoUsuario Tipo do Usuário criador da Mensagem.
 * @param criadoEm Data e Horário da criação da Mensagem.
 * @param arquivoId Identificador do Arquivo contido na Mensagem.
 * @param usuarioId Identificador do Usuário criador da Mensagem.
 */
public record MensagemResponseDTO(Long id, String texto, TipoUsuario tipoUsuario, LocalDateTime criadoEm,
                                  Long arquivoId, Long usuarioId) {
}
