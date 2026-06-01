package com.coliv.coliv_backend.Modulos.Cards.CardColega.Nucleo;

import jakarta.persistence.*;

@Entity
@Table(name = "card_colega")
public class CardColega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float classificacao;

    @Column(name = "colega_id")
    private Long colegaId;

    protected CardColega() {
    }

    public Long getId() {
        return id;
    }

    public Float getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Float classificacao) {
        this.classificacao = classificacao;
    }

    public Long getColegaId() {
        return colegaId;
    }

    public void setColegaId(Long colegaId) {
        this.colegaId = colegaId;
    }

    public void setId(long id) {
        this.id = id;
    }
}