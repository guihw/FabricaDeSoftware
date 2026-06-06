package com.coliv.coliv_backend.Modulos.Avaliacao.Contratos;

public record AvaliacaoResponse(

        Long id,
        Long cardId,
        Long usuarioId,
        Float valorClassificacao

) {
}