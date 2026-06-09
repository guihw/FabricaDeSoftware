package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "despesa")
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valor;

    private LocalDateTime dataVencimento;

    private String descricao;

    @ElementCollection
    @CollectionTable(
            name = "despesa_pago",
            joinColumns = @JoinColumn(name = "despesa_id")
    )
    @Column(name = "usuario_id")
    private List<Long> pago = new ArrayList<>();

    protected Despesa() {
    }

    public Long getId() {
        return id;
    }

    public Double getValor() {
        return valor;
    }

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Long> getPago() {
        return pago;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPago(List<Long> pago) {
        this.pago = pago;
    }
}