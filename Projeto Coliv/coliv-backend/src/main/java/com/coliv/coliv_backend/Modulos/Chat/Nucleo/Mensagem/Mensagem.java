package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sequencial_id", nullable = false)
    private Long sequencialId;
    private String texto;
    @Column(name = "tipo_usuario", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "grupo_id")
    private Long grupoId;
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
    @Column(name = "arquivo_id")
    private Long arquivoId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    public Mensagem() {
        this.criadoEm = LocalDateTime.now();
    }

    private Mensagem(Builder builder) {
        this.sequencialId = builder.sequencialId;
        this.texto = builder.texto;
        this.tipoUsuario = builder.tipoUsuario;
        this.chatId = builder.chatId;
        this.grupoId = builder.grupoId;
        this.criadoEm = builder.criadoEm;
        this.atualizadoEm = builder.atualizadoEm;
        this.arquivoId = builder.arquivoId;
        this.usuarioId = builder.usuarioId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencialId() {
        return sequencialId;
    }

    public void setSequencialId(Long sequencialId) {
        this.sequencialId = sequencialId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Long getArquivoId() {
        return arquivoId;
    }

    public void setArquivoId(Long arquivoId) {
        this.arquivoId = arquivoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public static class Builder {
        private Long sequencialId;
        private String texto;
        private TipoUsuario tipoUsuario;
        private Long chatId;
        private Long grupoId;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
        private Long arquivoId;
        private Long usuarioId;

        public Builder sequencialId(Long sequencialId) {
            this.sequencialId = sequencialId;
            return this;
        }

        public Builder texto(String texto) {
            this.texto = texto;
            return this;
        }

        public Builder tipoUsuario(TipoUsuario tipoUsuario) {
            this.tipoUsuario = tipoUsuario;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder grupoId(Long grupoId) {
            this.grupoId = grupoId;
            return this;
        }

        public Builder criadoEm(LocalDateTime criadoEm) {
            this.criadoEm = criadoEm;
            return this;
        }

        public Builder atualizadoEm(LocalDateTime atualizadoEm) {
            this.atualizadoEm = atualizadoEm;
            return this;
        }

        public Builder arquivoId(Long arquivoId) {
            this.arquivoId = arquivoId;
            return this;
        }

        public Builder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public Mensagem build() {
            return new Mensagem(this);
        }
    }
}