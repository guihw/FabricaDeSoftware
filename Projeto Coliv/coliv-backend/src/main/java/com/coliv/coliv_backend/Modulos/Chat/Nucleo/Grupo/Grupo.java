package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo;

import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro.Membro;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo.MensagemGrupo;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "anfitriao_id", nullable = false, unique = true)
    private Long anfitriaoId;
    @Column(name = "nome")
    private String nomeGrupo;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Membro> membros;
    @OneToMany(cascade = CascadeType.ALL)
    private List<MensagemGrupo> mensagens;

    public Grupo () {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnfitriaoId() {
        return anfitriaoId;
    }

    public void setAnfitriaoId(Long anfitriaoId) {
        this.anfitriaoId = anfitriaoId;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public List<Membro> getMembros() {
        return membros;
    }

    public void setMembros(List<Membro> membros) {
        this.membros = membros;
    }

    public void addMembro(Membro membro) {
        this.membros.add(membro);
    }

    public List<MensagemGrupo> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<MensagemGrupo> mensagens) {
        this.mensagens = mensagens;
    }

    public void addMensagem(MensagemGrupo mensagem) {this.mensagens.add(mensagem);}
}