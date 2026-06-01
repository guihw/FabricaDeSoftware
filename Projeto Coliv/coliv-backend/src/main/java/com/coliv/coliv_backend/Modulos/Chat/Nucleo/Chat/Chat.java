package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite.Convite;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.Mensagem;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Mensagem> mensagens = new ArrayList<>();
    @Column(name = "anfitriao_id", nullable = false)
    private Long anfitriaoId;
    @Column(name = "colega_id", nullable = false)
    private Long colegaId;

    Chat () {}

    private Chat(Builder builder) {
        this.id = builder.id;
        this.mensagens = builder.mensagens;
        this.anfitriaoId = builder.anfitriaoId;
        this.colegaId = builder.colegaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Mensagem> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<Mensagem> mensagens) {
        this.mensagens = mensagens;
    }

    public void addMensagens(Mensagem mensagem) {
        this.mensagens.add(mensagem);
    }

    public Long getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(Long anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }

    public Long getColegaId() {
        return colegaId;
    }

    public void setColegaId(Long colegaId) {
        this.colegaId = colegaId;
    }

    public static class Builder {
        private Long id;
        private List<Mensagem> mensagens;
        private Long anfitriaoId;
        private Long colegaId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder mensagens(List<Mensagem> mensagens) {
            this.mensagens = mensagens;
            return this;
        }

        public Builder anfitriaoId(Long anfitriaoId) {
            this.anfitriaoId = anfitriaoId;
            return this;
        }

        public Builder colegaId(Long colegaId) {
            this.colegaId = colegaId;
            return this;
        }

        public Chat build() {
            return new Chat(this);
        }
    }
}