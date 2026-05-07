package com.coliv.coliv_backend.Modulos.Usuarios.Contracts;

public class UsuarioDTO {
    private String nome;
    private String email;

    public UsuarioDTO () {}

    public UsuarioDTO(String email, String nome) {
        this.email = email;
        this.nome = nome;
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
}
