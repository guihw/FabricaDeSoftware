package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import jakarta.persistence.*;

@Entity
@Table(name = "dados_imovel")
class DadosImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "localizacao")
    private String localizacao;
    @Column(name = "qtd_quartos")
    private int quartos;
    @Column(name = "anfitriao_id")
    private Long anfitriaoId;

    DadosImovel () {}

    public DadosImovel(String descricao, String localizacao, int quartos) {
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.quartos = quartos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public int getQuartos() {
        return quartos;
    }

    public void setQuartos(int quartos) {
        this.quartos = quartos;
    }

    public Long getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(Long anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }
}
