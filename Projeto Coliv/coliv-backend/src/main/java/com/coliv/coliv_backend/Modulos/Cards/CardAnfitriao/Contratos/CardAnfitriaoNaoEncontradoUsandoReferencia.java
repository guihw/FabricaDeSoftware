package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos;

public class CardAnfitriaoNaoEncontradoUsandoReferencia extends RuntimeException {
    public CardAnfitriaoNaoEncontradoUsandoReferencia(Long id) {
        super("Nenhum Card de Anfitrião encontrado utilizando o id " + id + " como Referência");
    }
}
