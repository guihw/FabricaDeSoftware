package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Convite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "valor_definido", nullable = false)
    private BigDecimal valorDefinido;
    @Column(name = "convite_status")
    @Enumerated(EnumType.STRING)
    private ConviteStatus conviteStatus;
    @Column(name = "condicoes_iniciais")
    private String condicoesIniciais;
    @Column(name = "data_inicio", updatable = false)
    private LocalDateTime dataInicio;
    @OneToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    public Convite() {}

    public Convite(Builder builder) {
        this.id = builder.id;
        this.valorDefinido = builder.valorDefinido;
        this.conviteStatus = builder.conviteStatus;
        this.condicoesIniciais = builder.condicoesIniciais;
        this.dataInicio = builder.dataInicio;
        this.chat = builder.chat;
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

    public BigDecimal getValorDefinido() {
        return valorDefinido;
    }

    public void setValorDefinido(BigDecimal valorDefinido) {
        this.valorDefinido = valorDefinido;
    }

    public String getCondicoesIniciais() {
        return condicoesIniciais;
    }

    public void setCondicoesIniciais(String condicoesIniciais) {
        this.condicoesIniciais = condicoesIniciais;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public static class Builder {
        private Long id;
        private BigDecimal valorDefinido;
        private ConviteStatus conviteStatus;
        private String condicoesIniciais;
        private LocalDateTime dataInicio;
        private Chat chat;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder valorDefinido(BigDecimal valorDefinido) {
            this.valorDefinido = valorDefinido;
            return this;
        }

        public Builder conviteStatus(ConviteStatus conviteStatus) {
            this.conviteStatus = conviteStatus;
            return this;
        }

        public Builder condicoesIniciais(String condicoesIniciais) {
            this.condicoesIniciais = condicoesIniciais;
            return this;
        }

        public Builder dataInicio(LocalDateTime dataInicio) {
            this.dataInicio = dataInicio;
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