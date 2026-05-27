package com.coliv.coliv_backend.Modulos.Chat.Nucleo;

import jakarta.persistence.*;

import java.util.List;

@Entity
class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lista_usuario_id")
    private List<Long> listaUsuarioId;

    Chat () {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getUsuarioListId() {
        return listaUsuarioId;
    }

    public void setUsuarioListId(List<Long> usuarioListId) {
        this.listaUsuarioId = usuarioListId;
    }

    public void addToUsuarioListId(Long id) {
        this.listaUsuarioId.add(id);
    }
}