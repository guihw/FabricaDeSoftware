package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Usuario;
import jakarta.persistence.Entity;

@Entity
public class Colega extends Usuario {
    String descricao;
    Float classificacao;
    Long preferenciaColegaId;

   public Colega(){

   }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getPreferenciaColegaId() {
        return preferenciaColegaId;
    }

    public void setPreferenciaColegaId(Long preferenciaColegaId) {
        this.preferenciaColegaId = preferenciaColegaId;
    }

    public Float getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Float classificacao) {
        this.classificacao = classificacao;
    }
}
