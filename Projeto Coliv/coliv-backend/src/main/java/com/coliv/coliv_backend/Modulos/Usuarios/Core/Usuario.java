package com.coliv.coliv_backend.Modulos.Usuarios.Core;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String cpf;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senha;
    @Column(name = "possui_plano_id")
    private boolean possuiPlano = false;
    @Column(name = "foto_perfil_id")
    private Long fotoPerfil;

    public Usuario(){}

    public Usuario(String cpf, String nome, String email, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isPossuiPlano() {
        return possuiPlano;
    }

    public void setPossuiPlano(boolean possuiPlano) {
        this.possuiPlano = possuiPlano;
    }

    public Long getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Long fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
