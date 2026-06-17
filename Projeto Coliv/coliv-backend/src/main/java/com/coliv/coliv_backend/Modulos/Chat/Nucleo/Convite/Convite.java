package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * <p>
 *    <b>
 *       Entidade responsável pela formalização da intenção de um usuário {@code Anfitriao} em
 *       hospedar um usuário {@code Colega}.
 *    </b>
 * </p>
 *
 * <p>
 *    Essa entidade não lida com aspectos legais e serve como ponte a outras funções do Sistema.
 * </p>
 *
 * @author Miguel Lima
 */
@Entity
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "convite_status")
    @Enumerated(EnumType.STRING)
    private ConviteStatus conviteStatus;
//    @Column(name = "match_id") // Adicionar nullable = false pós Match class ser adicionada
//    private Long matchId;
    @Column(name = "texto")
    private String texto;
    @Column(name = "anfitriao_id")
    private Long anfitriaoId;
    @Column(name = "colega_id")
    private Long colegaId;
    @Column(name = "data_inicio", updatable = false)
    private LocalDateTime criadoEm;
    @Column(name = "data_atualizacao")
    private LocalDateTime atualizadoEm;
    @OneToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public Convite() {
        this.criadoEm = LocalDateTime.now();
    }

    public Convite(Builder builder) {
        this.id = builder.id;
        this.conviteStatus = builder.conviteStatus;
        this.texto = builder.texto;
        this.criadoEm = builder.criadoEm;
        this.chat = builder.chat;
        this.anfitriaoId = builder.anfitriaoId;
        this.colegaId = builder.colegaId;
//        this.matchId = builder.matchId;
        this.atualizadoEm = builder.atualizadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConviteStatus getConviteStatus() {
        return conviteStatus;
    }

    public void setConviteStatus(ConviteStatus conviteStatus) {
        this.conviteStatus = conviteStatus;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

//    public Long getMatchId() {
//        return matchId;
//    }
//
//    public void setMatchId(Long matchId) {
//        this.matchId = matchId;
//    }

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

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public static class Builder {
        private Long id;
        private ConviteStatus conviteStatus;
        private String texto;
        private LocalDateTime criadoEm;
        private Chat chat;
//        private Long matchId;
        private Long anfitriaoId;
        private Long colegaId;
        private LocalDateTime atualizadoEm;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

//        public Builder matchId(Long matchId) {
//            this.matchId = matchId;
//            return this;
//        }

        public Builder anfitriaoId(Long anfitriaoId) {
            this.anfitriaoId = anfitriaoId;
            return this;
        }

        public Builder colegaId(Long colegaId) {
            this.colegaId = colegaId;
            return this;
        }

        public Builder conviteStatus(ConviteStatus conviteStatus) {
            this.conviteStatus = conviteStatus;
            return this;
        }

        public Builder texto(String texto) {
            this.texto = texto;
            return this;
        }

        public Builder criadoEm(LocalDateTime criadoEm) {
            this.criadoEm = criadoEm;
            return this;
        }

        public Builder atualizadoEm(LocalDateTime atualizadoEm) {
            this.atualizadoEm = this.atualizadoEm;
            return this;
        }

        public Builder chat(Chat chat) {
            this.chat = chat;
            return this;
        }

        public Convite build() {
            return new Convite(this);
        }
    }
}