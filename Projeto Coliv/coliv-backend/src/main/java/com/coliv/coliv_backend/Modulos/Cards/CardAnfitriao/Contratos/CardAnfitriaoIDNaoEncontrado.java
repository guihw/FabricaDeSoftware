package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos;

public class CardAnfitriaoIDNaoEncontrado extends RuntimeException {
    public CardAnfitriaoIDNaoEncontrado(Long id) {
        super("Card de Anfitrião de Id " + id + " não encontrado . . .");
    }
}
