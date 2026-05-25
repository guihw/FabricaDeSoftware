package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "preferencias_colega")
public class PreferenciasColega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "preco_minimo")
    private BigDecimal precoMinimo;

    @Column(name = "preco_maximo")
    private BigDecimal precoMaximo;

    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "horario_sono")
    private LocalTime horarioDeSono;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_sociabilidade")
    private NivelDeSociabilidade nivelDeSociabilidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_limpeza")
    private NivelDeLimpeza nivelDeLimpeza;

    @Enumerated(EnumType.STRING)
    @Column(name = "habito_trabalho")
    private HabitoDeTrabalho habitoDeTrabalho;

    @Column(name = "aceita_animais")
    private boolean aceitaAnimais;

    @Column(name = "colega_id")
    private Long colegaId;

    protected PreferenciasColega() {
    }

    private PreferenciasColega(Builder builder) {
        this.precoMinimo = builder.precoMinimo;
        this.precoMaximo = builder.precoMaximo;
        this.localizacao = builder.localizacao;
        this.horarioDeSono = builder.horarioDeSono;
        this.nivelDeSociabilidade = builder.nivelDeSociabilidade;
        this.nivelDeLimpeza = builder.nivelDeLimpeza;
        this.habitoDeTrabalho = builder.habitoDeTrabalho;
        this.aceitaAnimais = builder.aceitaAnimais;
        this.colegaId = builder.colegaId;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPrecoMinimo() {
        return precoMinimo;
    }

    public void setPrecoMinimo(BigDecimal precoMinimo) {
        this.precoMinimo = precoMinimo;
    }

    public BigDecimal getPrecoMaximo() {
        return precoMaximo;
    }

    public void setPrecoMaximo(BigDecimal precoMaximo) {
        this.precoMaximo = precoMaximo;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public LocalTime getHorarioDeSono() {
        return horarioDeSono;
    }

    public void setHorarioDeSono(LocalTime horarioDeSono) {
        this.horarioDeSono = horarioDeSono;
    }

    public NivelDeSociabilidade getNivelDeSociabilidade() {
        return nivelDeSociabilidade;
    }

    public void setNivelDeSociabilidade(NivelDeSociabilidade nivelDeSociabilidade) {
        this.nivelDeSociabilidade = nivelDeSociabilidade;
    }

    public NivelDeLimpeza getNivelDeLimpeza() {
        return nivelDeLimpeza;
    }

    public void setNivelDeLimpeza(NivelDeLimpeza nivelDeLimpeza) {
        this.nivelDeLimpeza = nivelDeLimpeza;
    }

    public HabitoDeTrabalho getHabitoDeTrabalho() {
        return habitoDeTrabalho;
    }

    public void setHabitoDeTrabalho(HabitoDeTrabalho habitoDeTrabalho) {
        this.habitoDeTrabalho = habitoDeTrabalho;
    }

    public boolean isAceitaAnimais() {
        return aceitaAnimais;
    }

    public void setAceitaAnimais(boolean aceitaAnimais) {
        this.aceitaAnimais = aceitaAnimais;
    }

    public Long getColegaId() {
        return colegaId;
    }

    public void setColegaId(Long colegaId) {
        this.colegaId = colegaId;
    }

    public static class Builder {

        private BigDecimal precoMinimo;
        private BigDecimal precoMaximo;
        private String localizacao;
        private LocalTime horarioDeSono;
        private NivelDeSociabilidade nivelDeSociabilidade;
        private NivelDeLimpeza nivelDeLimpeza;
        private HabitoDeTrabalho habitoDeTrabalho;
        private boolean aceitaAnimais;
        private Long colegaId;

        public Builder precoMinimo(BigDecimal precoMinimo) {
            this.precoMinimo = precoMinimo;
            return this;
        }

        public Builder precoMaximo(BigDecimal precoMaximo) {
            this.precoMaximo = precoMaximo;
            return this;
        }

        public Builder localizacao(String localizacao) {
            this.localizacao = localizacao;
            return this;
        }

        public Builder horarioDeSono(LocalTime horarioDeSono) {
            this.horarioDeSono = horarioDeSono;
            return this;
        }

        public Builder nivelDeSociabilidade(NivelDeSociabilidade nivelDeSociabilidade) {
            this.nivelDeSociabilidade = nivelDeSociabilidade;
            return this;
        }

        public Builder nivelDeLimpeza(NivelDeLimpeza nivelDeLimpeza) {
            this.nivelDeLimpeza = nivelDeLimpeza;
            return this;
        }

        public Builder habitoDeTrabalho(HabitoDeTrabalho habitoDeTrabalho) {
            this.habitoDeTrabalho = habitoDeTrabalho;
            return this;
        }

        public Builder aceitaAnimais(boolean aceitaAnimais) {
            this.aceitaAnimais = aceitaAnimais;
            return this;
        }

        public Builder colegaId(Long colegaId) {
            this.colegaId = colegaId;
            return this;
        }

        public PreferenciasColega build() {
            return new PreferenciasColega(this);
        }
    }
}