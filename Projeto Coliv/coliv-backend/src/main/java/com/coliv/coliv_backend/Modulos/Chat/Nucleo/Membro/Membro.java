package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo.Grupo;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.persistence.*;

@Entity
public class Membro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "tipo_usuario", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;
    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    public Membro () {}

    private Membro (Builder builder) {
        this.id = builder.id;
        this.usuarioId = builder.usuarioId;
        this.tipoUsuario = builder.tipoUsuario;
        this.grupo = builder.grupo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

     public static class Builder {
        private Long id;
        private Long usuarioId;
        private TipoUsuario tipoUsuario;
        private Grupo grupo;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public Builder tipoUsuario(TipoUsuario tipoUsuario) {
            this.tipoUsuario = tipoUsuario;
            return this;
        }

        public Builder grupo(Grupo grupo) {
            this.grupo = grupo;
            return this;
        }

        public Membro build() {
            return new Membro(this);
        }
    }
}