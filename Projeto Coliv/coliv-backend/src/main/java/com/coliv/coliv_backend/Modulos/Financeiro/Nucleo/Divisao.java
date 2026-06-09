package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import jakarta.persistence.*;

@Entity
@Table(name = "divisao")
public class Divisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long despesaId;

    private Long usuarioId;

    private Long arquivoId;

    private Double valor;

    protected Divisao() {
    }

    public Long getId() {
        return id;
    }

    public Long getDespesaId() {
        return despesaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getArquivoId() {
        return arquivoId;
    }

    public Double getValor() {
        return valor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDespesaId(Long despesaId) {
        this.despesaId = despesaId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setArquivoId(Long arquivoId) {
        this.arquivoId = arquivoId;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}