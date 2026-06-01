package com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos;

public class CardColegaIDNaoEncontrado extends RuntimeException {

    public CardColegaIDNaoEncontrado(Long id) {
        super("Card de Colega com id " + id + " não encontrado.");
    }
}