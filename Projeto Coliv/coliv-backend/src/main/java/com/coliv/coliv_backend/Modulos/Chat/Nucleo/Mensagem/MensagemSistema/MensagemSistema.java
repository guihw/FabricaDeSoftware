package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemSistema;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.Mensagem;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "mensagem_sistema")
public class MensagemSistema extends Mensagem {

    public MensagemSistema () {
        super();
    }
}