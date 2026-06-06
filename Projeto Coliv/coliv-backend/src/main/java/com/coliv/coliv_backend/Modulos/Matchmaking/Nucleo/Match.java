package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matchmaking")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "colega_id")
    private Long colegaId;

    @Column(name = "anfitriao_id")
    private Long anfitriaoId;

    @Enumerated(EnumType.STRING)
    private StatusMatch status;

    private LocalDateTime criadoEm;

    protected Match() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getColegaId() {
        return colegaId;
    }

    public void setColegaId(Long colegaId) {
        this.colegaId = colegaId;
    }

    public Long getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(Long anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }

    public StatusMatch getStatus() {
        return status;
    }

    public void setStatus(StatusMatch status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}