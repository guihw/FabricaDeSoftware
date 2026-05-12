package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Usuario;
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

    public Anfitriao(String nome, String cpf, String email, String senha, Long fotoPerfil) {
        super(nome, cpf, email, senha, fotoPerfil);
    }

    public Anfitriao(Long id, String nome, String cpf, String email, String senha, boolean possuiPlano, Long fotoPerfil, Long dadosImoveId, Long preferenciasId) {
        super(id, nome, cpf, email, senha, possuiPlano, fotoPerfil);
        this.dadosImoveId = dadosImoveId;
        this.preferenciasId = preferenciasId;
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
