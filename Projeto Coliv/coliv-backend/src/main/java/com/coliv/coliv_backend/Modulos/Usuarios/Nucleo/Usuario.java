package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String cpf;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senha;
    @Column(name = "possui_plano_id")
    private boolean possuiPlano = false;

    public Usuario(){}

    public Usuario(String nome, String cpf, String email, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(Long id, String nome, String cpf, String email, String senha, boolean possuiPlano) {
        this.email = email;
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.senha = senha;
        this.possuiPlano = possuiPlano;
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

    public boolean getPossuiPlano() {
        return possuiPlano;
    }

    public void setPossuiPlano(boolean possuiPlano) {
        this.possuiPlano = possuiPlano;
    }
}
