package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.Mensagem;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "mensagem_grupo")
public class MensagemGrupo extends Mensagem {

    @Column(name = "grupo_id")
    private Long grupoId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "tipo_usuario", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    public MensagemGrupo () {
        super();
    }

    public MensagemGrupo(Long sequencialId, String texto, Long arquivoId, Long grupoId, Long usuarioId,
                         TipoUsuario tipoUsuario) {
        super(sequencialId, texto, arquivoId);
        this.grupoId = grupoId;
        this.usuarioId = usuarioId;
        this.tipoUsuario = tipoUsuario;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}