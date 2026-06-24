package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sequencial_id", nullable = false)
    private Long sequencialId;
    private String texto;
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
    @Column(name = "arquivo_id")
    private Long arquivoId;

    public Mensagem () {
        this.criadoEm = LocalDateTime.now();
    }

    public Mensagem(String texto, Long arquivoId) {
        this.texto = texto;
        this.arquivoId = arquivoId;
        this.criadoEm = LocalDateTime.now();
    }

    public Mensagem(Long sequencialId, String texto, Long arquivoId) {
        this.sequencialId = sequencialId;
        this.texto = texto;
        this.criadoEm = LocalDateTime.now();
        this.arquivoId = arquivoId;
    }

    public Mensagem(Long id, Long sequencialId, String texto, Long arquivoId) {
        this.id = id;
        this.sequencialId = sequencialId;
        this.texto = texto;
        this.criadoEm = LocalDateTime.now();
        this.arquivoId = arquivoId;
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
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
}