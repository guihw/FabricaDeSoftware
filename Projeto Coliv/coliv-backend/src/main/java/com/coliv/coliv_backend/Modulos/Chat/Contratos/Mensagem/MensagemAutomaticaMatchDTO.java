package com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta.MensagemDireta;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEvento;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;

import java.time.LocalDateTime;

/**
 * <p><b>DTO que carrega informações sobre uma nova {@link MensagemDireta}</b></p>
 * <p>Criado quando um evento {@link MatchEvento} é iniciado no Sistema.</p>
 * <br><br>
 * @param texto Texto que será adicionado a nova Mensagem.
 * @param tipoUsuario Tipo do usuário a qual a nova Mensagem será anexada.
 * @param chat Chat a qual a nova Mensagem pertence.
 * @param criadoEm Data e Horário da criação referênte a Mensagem.
 * @param usuarioId Identificador do Usuário.
 * @see Chat
 * @see TipoUsuario
 * @see MensagemAutomaticaDTO
 */
public record MensagemAutomaticaMatchDTO(String texto, TipoUsuario tipoUsuario, Chat chat, LocalDateTime criadoEm,
                                         Long usuarioId) {
}
