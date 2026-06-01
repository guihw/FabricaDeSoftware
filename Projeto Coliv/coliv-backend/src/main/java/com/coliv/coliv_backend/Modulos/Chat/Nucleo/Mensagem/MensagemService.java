package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatCriadoViaMatch;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemNaoEncontradaUsandoReferencias;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.ChatRepository;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository msgRepository;
    @Autowired
    private ChatRepository chatRepository;

    public List<MensagemResponseDTO> listar() {
        return msgRepository.findAll().stream().map(mensagem ->
                new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                        mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorChatId(Long chatId) {
        return msgRepository.findByChatId(chatId).stream().map(mensagem ->
                new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                        mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorUsuarioId(Long usuarioId, TipoUsuario tipoUsuario) {
        return msgRepository.findByUsuarioIdAndTipoUsuario(usuarioId, tipoUsuario).stream().map(mensagem ->
                new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                        mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorChatEUsuario(Long chatId, Long usuarioId, TipoUsuario tipoUsuario) {
        return msgRepository.findByChatIdAndUsuarioIdAndTipoUsuario(chatId, usuarioId, tipoUsuario).stream().map(
                mensagem -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(),
                        mensagem.getTipoUsuario(), mensagem.getCriadoEm(), mensagem.getArquivoId(),
                        mensagem.getUsuarioId())).toList();
    }

    public List<MensagemResponseDTO> buscarPorChatETexto(Long chatId, String texto) {
        return msgRepository.findByChatIdAndTextoContainingIgnoreCase(chatId, texto).stream().map(
                mensagem -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(),
                        mensagem.getTipoUsuario(), mensagem.getCriadoEm(), mensagem.getArquivoId(),
                        mensagem.getUsuarioId())).toList();
    }

    public MensagemResponseDTO criarMensagem(Long usuarioId, Long chatId, MensagemRequestDTO dto) {
        Long max = msgRepository.findMaxSequencialIdByChatId(chatId);

        Mensagem mensagem = new Mensagem.Builder().
                texto(dto.texto()).
                tipoUsuario(dto.tipoUsuario()).
                chatId(chatId).
                sequencialId(max + 1).
                arquivoId(dto.arquivoId()).
                usuarioId(usuarioId).
                criadoEm(LocalDateTime.now()).
                build();

        mensagem = msgRepository.save(mensagem);
        Chat chat =  chatRepository.findById(chatId).orElseThrow(() -> new ChatIDNaoEncontrado(chatId));
        chat.addMensagens(mensagem);
        chatRepository.save(chat);

        return new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId());
    }

    @Transactional
    public MensagemResponseDTO editarMensagem(Long sequencialId, Long chatId, Long usuarioId, MensagemRequestDTO dto) {
        Mensagem mensagem = msgRepository.findBySequencialIdAndChatIdAndUsuarioId(sequencialId, chatId, usuarioId).
                orElseThrow(() -> new MensagemNaoEncontradaUsandoReferencias(sequencialId, chatId, usuarioId));

        if (dto.texto() != null && !dto.texto().isBlank()) {
            mensagem.setTexto(dto.texto());
        }

        mensagem.setArquivoId(dto.arquivoId());
        mensagem.setAtualizadoEm(LocalDateTime.now());

        mensagem = msgRepository.save(mensagem);

        return new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId());
    }

    public void excluirMensagem(Long sequencialId, Long chatId, Long usuarioId) {
        Mensagem mensagem = msgRepository.findBySequencialIdAndChatIdAndUsuarioId(sequencialId, chatId, usuarioId).
                orElseThrow(() -> new MensagemNaoEncontradaUsandoReferencias(sequencialId, chatId, usuarioId));
        msgRepository.deleteById(mensagem.getId());
    }

    @Transactional
    public void limparMensagens(Long chatId) {
       msgRepository.deleteAllByChatId(chatId);
    }

    @Transactional
    public Mensagem mensagemAutomaticaMatchEvento(ChatCriadoViaMatch dto) {
        Long max = msgRepository.findMaxSequencialIdByChatId(dto.dto().chat().getId());

        Mensagem mensagem = msgRepository.save(new Mensagem.Builder().
                texto(dto.dto().texto()).
                tipoUsuario(dto.dto().tipoUsuario()).
                chatId(dto.dto().chat().getId()).
                criadoEm(dto.dto().criadoEm()).
                sequencialId(max + 1).
                usuarioId(dto.dto().usuarioId()).
                build());

        return msgRepository.save(mensagem);
    }
}