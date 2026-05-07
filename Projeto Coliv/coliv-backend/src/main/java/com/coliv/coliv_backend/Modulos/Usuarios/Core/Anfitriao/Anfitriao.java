package com.coliv.coliv_backend.Modulos.Usuarios.Core.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Core.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabela_anfitriao")
class Anfitriao extends Usuario {

    @Column(name = "dados_imovel_id")
    private Long dadosImoveId;
    @Column(name = "preferencias_id")
    private Long preferenciasId;

    public Anfitriao () {}

    public Anfitriao(String cpf, String nome, String email, String senha) {
        super(cpf, nome, email, senha);
    }

    public Long getDadosImoveId() {
        return dadosImoveId;
    }

    public void setDadosImoveId(Long dadosImoveId) {
        this.dadosImoveId = dadosImoveId;
    }

    public Long getPreferenciasId() {
        return preferenciasId;
    }

    public void setPreferenciasId(Long preferenciasId) {
        this.preferenciasId = preferenciasId;
    }
}
