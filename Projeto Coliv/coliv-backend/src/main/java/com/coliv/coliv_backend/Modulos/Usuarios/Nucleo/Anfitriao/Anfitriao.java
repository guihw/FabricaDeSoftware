package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tabela_anfitriao")
class Anfitriao extends Usuario {

    public Anfitriao () {}

    public Anfitriao(String nome, String cpf, String email, String senha, Long fotoPerfil) {
        super(nome, cpf, email, senha, fotoPerfil);
    }

    public Anfitriao(Long id, String nome, String cpf, String email, String senha, boolean possuiPlano, Long fotoPerfil) {
        super(id, nome, cpf, email, senha, possuiPlano, fotoPerfil);
    }
}
