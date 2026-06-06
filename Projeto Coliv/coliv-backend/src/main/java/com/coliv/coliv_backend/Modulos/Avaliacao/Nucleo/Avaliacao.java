package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import jakarta.persistence.*;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "valor_classificacao")
    private Float valorClassificacao;

    protected Avaliacao() {
    }

    public Long getId() {
        return id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Float getValorClassificacao() {
        return valorClassificacao;
    }

    public void setValorClassificacao(Float valorClassificacao) {
        this.valorClassificacao = valorClassificacao;
    }

    public void setId(Long id) {
        this.id = id;
    }
}