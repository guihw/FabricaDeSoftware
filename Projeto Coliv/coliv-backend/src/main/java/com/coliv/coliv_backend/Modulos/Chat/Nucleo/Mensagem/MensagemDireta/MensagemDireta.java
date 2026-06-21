package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.Mensagem;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "mensagem_direta")
public class MensagemDireta extends Mensagem {


    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "tipo_usuario", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    public MensagemDireta() {
        super();
    }

    public MensagemDireta(Long sequencialId, String texto, Long arquivoId, Long chatId, Long usuarioId,
                          TipoUsuario tipoUsuario) {
        super(sequencialId, texto, arquivoId);
        this.chatId = chatId;
        this.usuarioId = usuarioId;
        this.tipoUsuario = tipoUsuario;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}