package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    /**
     * "ANFITRIAO" ou "COLEGA" - necessário porque Anfitriao e Colega são
     * entidades separadas; sem isso o webhook não saberia em qual delas
     * ativar o plano.
     */
    @Column(nullable = false)
    private String tipoUsuario;

    @Column(unique = true)
    private String billingId;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    protected Pagamento() {
    }

    public Pagamento(Long usuarioId, String tipoUsuario) {
        this.usuarioId = usuarioId;
        this.tipoUsuario = tipoUsuario;
        this.status = StatusPagamento.PENDENTE;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getBillingId() {
        return billingId;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
        this.atualizadoEm = LocalDateTime.now();
    }
}
