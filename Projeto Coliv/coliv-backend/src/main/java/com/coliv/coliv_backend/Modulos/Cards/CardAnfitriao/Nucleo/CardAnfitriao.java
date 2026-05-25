package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "card_anfitriao")
class CardAnfitriao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double classificacao;
    @Column(name = "preco_mensal", nullable = false)
    private BigDecimal precoMensal = BigDecimal.ZERO;
    @Column(name = "lista_arquivos_id")
    private List<Long> arquivos;
    @Column(name = "anfitriao_id")
    private Long anfitriaoId;

    CardAnfitriao () {}

    public CardAnfitriao(BigDecimal precoMensal) {
        this.precoMensal = precoMensal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Double classificacao) {
        this.classificacao = classificacao;
    }

    public BigDecimal getPrecoMensal() {
        return precoMensal;
    }

    public void setPrecoMensal(BigDecimal precoMensal) {
        this.precoMensal = precoMensal;
    }

    public List<Long> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Long> arquivos) {
        this.arquivos = arquivos;
    }

    public Long getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(Long anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }
}
