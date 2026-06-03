package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import jakarta.persistence.*;

@Entity
@Table(name = "matchs")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long iniciador;

    @Column(name = "colega_id")
    private Long colegaId;

    @Column(name = "anfitriao_id")
    private Long anfitriaoId;

    protected Match() {
    }

    public Long getId() {
        return id;
    }

    public Long getIniciador() {
        return iniciador;
    }

    public void setIniciador(Long iniciador) {
        this.iniciador = iniciador;
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

    public void setId(Long id) {
        this.id = id;
    }
}