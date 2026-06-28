package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dados_imovel")
class DadosImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "qtd_quartos")
    private int quartos;

    @Column(name = "anfitriao_id")
    private Long anfitriaoId;

    @Column(name = "preco_mensal", precision = 10, scale = 2)
    private BigDecimal precoMensal;

    @Column(name = "tipo_vaga")
    private String tipoVaga;
    //Lista de IDs de comodidades selecionadas pelo anfitrião (ex: ["wifi", "academia", "pet", "piscina"]).
    @ElementCollection
    @CollectionTable(
            name = "dados_imovel_comodidades",
            joinColumns = @JoinColumn(name = "dados_imovel_id")
    )
    @Column(name = "comodidade")
    private List<String> comodidades = new ArrayList<>();


    DadosImovel() {}

    public DadosImovel(String descricao, String localizacao, int quartos) {
        this.descricao   = descricao;
        this.localizacao = localizacao;
        this.quartos     = quartos;
    }

    public Long getId()                   { return id; }
    public void setId(Long id)            { this.id = id; }

    public String getDescricao()          { return descricao; }
    public void setDescricao(String d)    { this.descricao = d; }

    public String getLocalizacao()        { return localizacao; }
    public void setLocalizacao(String l)  { this.localizacao = l; }

    public int getQuartos()               { return quartos; }
    public void setQuartos(int q)         { this.quartos = q; }

    public Long getAnfitriaoId()          { return anfitriaoId; }
    public void setAnfitriaoId(Long id)   { this.anfitriaoId = id; }

    public BigDecimal getPrecoMensal()              { return precoMensal; }
    public void setPrecoMensal(BigDecimal preco)    { this.precoMensal = preco; }

    public String getTipoVaga()           { return tipoVaga; }
    public void setTipoVaga(String tv)    { this.tipoVaga = tv; }

    public List<String> getComodidades()            { return comodidades; }
    public void setComodidades(List<String> lista)  { this.comodidades = lista != null ? lista : new ArrayList<>(); }
}