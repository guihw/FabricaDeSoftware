package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

public class ColegaResponse {//DADOS QUE SAEM DA API
    private Long id;
    private String nome;
    private String email;

    public ColegaResponse(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
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
