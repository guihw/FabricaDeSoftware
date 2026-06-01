package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import jakarta.persistence.*;

@Entity
@Table(name = "arquivo")
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Enumerated(EnumType.STRING)
    private TipoArquivo type;

    protected Arquivo() {
    }

    public Arquivo(String url, TipoArquivo type) {
        this.url = url;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public TipoArquivo getType() {
        return type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(TipoArquivo type) {
        this.type = type;
    }
}