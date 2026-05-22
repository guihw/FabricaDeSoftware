package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "preferencias_anfitriao")
class PreferenciasAnfitriao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(name = "presenca_animais")
    private boolean presencaAnimais;
    @Column(name = "horarios_visita")
    private LocalTime horariosParaVisita;
    @Column(name = "politica_limpeza")
    private String politicaDeLimpeza;
    @Column(name = "regras_casa")
    private String regrasDaCasa;
    @Column(name = "perfil_colega_desejado")
    private String perfilColegaDesejado;
    @Column(name = "anfitriao_id")
    private Long anfitriaoId;

    PreferenciasAnfitriao() {}

    public PreferenciasAnfitriao(boolean presencaAnimais, LocalTime horariosParaVisita, String politicaDeLimpeza,
                                 String regrasDaCasa, String perfilColegaDesejado) {
        this.presencaAnimais = presencaAnimais;
        this.horariosParaVisita = horariosParaVisita;
        this.politicaDeLimpeza = politicaDeLimpeza;
        this.regrasDaCasa = regrasDaCasa;
        this.perfilColegaDesejado = perfilColegaDesejado;
    }

    public PreferenciasAnfitriao(boolean presencaAnimais, LocalTime horariosParaVisita, String politicaDeLimpeza,
                                 String regrasDaCasa, String perfilColegaDesejado, Long anfitriaoId) {
        this.presencaAnimais = presencaAnimais;
        this.horariosParaVisita = horariosParaVisita;
        this.politicaDeLimpeza = politicaDeLimpeza;
        this.regrasDaCasa = regrasDaCasa;
        this.perfilColegaDesejado = perfilColegaDesejado;
        this.anfitriaoId = anfitriaoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getPresencaAnimais() {
        return presencaAnimais;
    }

    public void setPresencaAnimais(boolean presencaAnimais) {
        this.presencaAnimais = presencaAnimais;
    }

    public LocalTime getHorariosParaVisita() {
        return horariosParaVisita;
    }

    public void setHorariosParaVisita(LocalTime horariosParaVisita) {
        this.horariosParaVisita = horariosParaVisita;
    }

    public String getPoliticaDeLimpeza() {
        return politicaDeLimpeza;
    }

    public void setPoliticaDeLimpeza(String politicaDeLimpeza) {
        this.politicaDeLimpeza = politicaDeLimpeza;
    }

    public String getRegrasDaCasa() {
        return regrasDaCasa;
    }

    public void setRegrasDaCasa(String regrasDaCasa) {
        this.regrasDaCasa = regrasDaCasa;
    }

    public String getPerfilColegaDesejado() {
        return perfilColegaDesejado;
    }

    public void setPerfilColegaDesejado(String perfilColegaDesejado) {
        this.perfilColegaDesejado = perfilColegaDesejado;
    }

    public Long getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(Long anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }
}
