package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo.Grupo;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo.GrupoRepository;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MensagemGrupoService {

    @Autowired
    private MensagemGrupoRepository msgRepository;
    @Autowired
    private GrupoRepository grupoRepository;

    public List<MensagemResponseDTO> listar() {
        return msgRepository.findAll().stream().map(mensagem -> new MensagemResponseDTO(
                mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(), mensagem.getCriadoEm(),
                mensagem.getArquivoId(), mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorGrupoId(Long grupoId) {
        return msgRepository.findByGrupoId(grupoId).stream().map(mensagem -> new MensagemResponseDTO(
                mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(), mensagem.getCriadoEm(),
                mensagem.getArquivoId(), mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorUsuarioId(Long usuarioId, TipoUsuario tipoUsuario) {
        return msgRepository.findByUsuarioIdAndTipoUsuario(usuarioId, tipoUsuario).stream().map(mensagem
                -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorGrupoEUsuario(Long grupoId, Long usuarioId, TipoUsuario tipoUsuario) {
        return msgRepository.findByGrupoIdAndUsuarioIdAndTipoUsuario(grupoId, usuarioId, tipoUsuario).stream().map(
                mensagem -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(),
                        mensagem.getTipoUsuario(), mensagem.getCriadoEm(), mensagem.getArquivoId(),
                        mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorChatETexto(Long grupoId, String texto) {
        return msgRepository.findByGrupoIdAndTextoContainingIgnoreCase(grupoId, texto).stream().map(
                mensagem -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(),
                mensagem.getTipoUsuario(), mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId())).
                toList();
    }

    public MensagemResponseDTO criarMensagem(Long usuarioId, Long grupoId, MensagemRequestDTO dto) {
        Long max = msgRepository.findMaxSequence(grupoId);

        MensagemGrupo mensagem = new MensagemGrupo(max + 1, dto.texto(), dto.arquivoId(), grupoId, usuarioId,
                dto.tipoUsuario());

        mensagem = msgRepository.save(mensagem);
        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow(() -> new GrupoIDNaoEncontrado(grupoId));
        if (grupo.getMensagens().isEmpty()) {
            MensagemGrupo m = mensagem;
            grupo.setMensagens(new ArrayList<>(){{add(m);}});
        } else {
            grupo.addMensagem(mensagem);
        }
        grupoRepository.save(grupo);

        return new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId());
    }
}