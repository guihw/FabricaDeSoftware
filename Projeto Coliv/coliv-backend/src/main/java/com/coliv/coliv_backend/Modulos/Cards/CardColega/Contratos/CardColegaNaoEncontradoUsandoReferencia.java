package com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos;

public class CardColegaNaoEncontradoUsandoReferencia extends RuntimeException {

    public CardColegaNaoEncontradoUsandoReferencia(Long colegaId) {
        super("Nenhum Card de Colega encontrado utilizando o id "
                + colegaId + " como referência.");
    }
}