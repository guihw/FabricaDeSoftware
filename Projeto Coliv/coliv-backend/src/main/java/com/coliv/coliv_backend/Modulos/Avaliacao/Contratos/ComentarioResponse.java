package com.coliv.coliv_backend.Modulos.Avaliacao.Contratos;

import java.time.LocalDateTime;

public record ComentarioResponse(

        Long id,
        Long cardId,
        Long usuarioId,
        Long parentId,
        LocalDateTime data,
        String texto

) {
}