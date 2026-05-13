package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "preferencias_anfitriao")
class PreferenciasAnfitriao {
    //Using PA for abbreviation.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(name = "presenca_animais")
    private boolean presencaAnimais;
    @Column(name = "horarios_visita")
    private String horariosParaVisita;
    @Column(name = "politica_limpeza")
    private String politicaDeLimpeza;
    @Column(name = "regras_casa")
    private String regrasDaCasa;
    @Column(name = "perfil_colega_desejado")
    private String perfilColegaDesejado;

    PreferenciasAnfitriao() {}

    public PreferenciasAnfitriao(boolean presencaAnimais, String horariosParaVisita, String politicaDeLimpeza, String regrasDaCasa, String perfilColegaDesejado) {
        this.presencaAnimais = presencaAnimais;
        this.horariosParaVisita = horariosParaVisita;
        this.politicaDeLimpeza = politicaDeLimpeza;
        this.regrasDaCasa = regrasDaCasa;
        this.perfilColegaDesejado = perfilColegaDesejado;
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

    public String getHorariosParaVisita() {
        return horariosParaVisita;
    }

    public void setHorariosParaVisita(String horariosParaVisita) {
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
}
