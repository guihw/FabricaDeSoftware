package com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite;

import java.time.LocalDateTime;

/**
 * <p><b>DTO</b> que carrega as informações de um {@code Convite}.</p>
 *
 * @param id Identificador de um <b>Convite</b>.
 * @param conviteStatus Representa o <b>Status</b> atual de um <b>Convite</b>.
 * @param texto Conteúdo <b>opcional</b> que pode ser adicionado a um <b>Convite</b> por um {@code Anfitriao}.
 * @param criadoEm Data de criação do <b>Convite</b>.
 * @param chatId Identificador do {@code Chat} a qual o <b>Convite</b> pertence.
 * @param matchId Identificador da entidade {@code Match}.
 * @param anfitriaoId Identificador do usuário {@code Anfitriao}.
 * @param colegaId Identificador do usuário {@code Colega}.
 * @param dataAtualizacao Data em que um <b>Convite</b> foi modificado.
 */
public record ConviteResponseDTO(
        Long id,
        ConviteStatus conviteStatus,
        String texto,
        LocalDateTime criadoEm,
        //Long matchId,
        Long anfitriaoId,
        Long colegaId,
        Long chatId,
        LocalDateTime dataAtualizacao
) {
}