package com.coliv.coliv_backend.Modulos.Avaliacao.Contratos;

public record ComentarioDTO(

        Long cardId,
        Long usuarioId,
        Long parentId,
        String texto

) {
}