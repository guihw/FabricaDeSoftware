package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "parent_id")
    private Long parentId;

    private LocalDateTime data;

    @Column(columnDefinition = "TEXT")
    private String texto;

    protected Comentario() {
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setId(Long id) {
        this.id = id;
    }
}