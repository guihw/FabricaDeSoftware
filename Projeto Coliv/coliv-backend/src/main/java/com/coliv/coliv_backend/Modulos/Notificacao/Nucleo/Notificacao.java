package com.coliv.coliv_backend.Modulos.Notificacao.Nucleo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacao tipo;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String mensagem;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    protected Notificacao() {}

    public Notificacao(Long usuarioId, TipoNotificacao tipo, String titulo, String mensagem, Long referenciaId) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.referenciaId = referenciaId;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public TipoNotificacao getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensagem() { return mensagem; }
    public Long getReferenciaId() { return referenciaId; }
    public boolean isLida() { return lida; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void marcarComoLida() { this.lida = true; }
}
