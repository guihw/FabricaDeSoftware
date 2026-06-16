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
    private List<MensagemGrupo> mensagensUsuario;

    public Grupo () {}

    private Grupo (Builder builder) {
        this.id = builder.id;
        this.anfitriaoId = builder.anfitriaoId;
        this.nomeGrupo = builder.nomeGrupo;
        this.membros = builder.membros;
        this.mensagensUsuario = builder.mensagensUsuario;
    }

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

    public List<MensagemGrupo> getMensagensUsuario() {
        return mensagensUsuario;
    }

    public void setMensagensUsuario(List<MensagemGrupo> mensagensUsuario) {
        this.mensagensUsuario = mensagensUsuario;
    }

    public void addMensagem(MensagemGrupo mensagem) {this.mensagensUsuario.add(mensagem);}

    public static class Builder {
        private Long id;
        private Long anfitriaoId;
        private String nomeGrupo;
        private List<Membro> membros;
        private List<MensagemGrupo> mensagensUsuario;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder anfitriaoId(Long anfitriaoId) {
            this.anfitriaoId = anfitriaoId;
            return this;
        }

        public Builder nomeGrupo(String nomeGrupo) {
            this.nomeGrupo = nomeGrupo;
            return this;
        }

        public Builder membros(List<Membro> membros) {
            this.membros = membros;
            return this;
        }

        public Builder mensagensUsuario(List<MensagemGrupo> mensagens) {
            this.mensagensUsuario = mensagens;
            return this;
        }

        public Grupo build () {
            return new Grupo(this);
        }
    }
}