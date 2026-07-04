package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatCriadoViaMatch;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemNaoEncontradaUsandoReferencias;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.ChatRepository;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.NovaMensagemEvent;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensagemDiretaService {

    @Autowired
    private MensagemDiretaRepository msgRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ApplicationEventPublisher publisher;

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
        return msgRepository.findByUsuarioIdAndTipoUsuario(usuarioId, tipoUsuario).stream().map(mensagem
                -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
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

    @Transactional
    public MensagemResponseDTO criarMensagem(Long usuarioId, Long chatId, MensagemRequestDTO dto) {
        Long max = msgRepository.findMaxSequencialIdByChatId(chatId);

        MensagemDireta mensagemDireta = new MensagemDireta(max + 1L, dto.texto(), dto.arquivoId(), chatId,
                usuarioId, dto.tipoUsuario());

        mensagemDireta = msgRepository.save(mensagemDireta);
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatIDNaoEncontrado(chatId));
        chat.addMensagens(mensagemDireta);
        chatRepository.save(chat);

        Long destinatarioId = dto.tipoUsuario() == TipoUsuario.ANFITRIAO
                ? chat.getColegaId()
                : chat.getAnfitriaoId();
        publisher.publishEvent(new NovaMensagemEvent(destinatarioId, chatId));

        return new MensagemResponseDTO(mensagemDireta.getSequencialId(), mensagemDireta.getTexto(),
                mensagemDireta.getTipoUsuario(), mensagemDireta.getCriadoEm(), mensagemDireta.getArquivoId(),
                mensagemDireta.getUsuarioId());
    }

    @Transactional
    public MensagemResponseDTO editarMensagem(Long sequencialId, Long chatId, Long usuarioId, MensagemRequestDTO dto) {
        MensagemDireta mensagemDireta = msgRepository.findBySequencialIdAndChatIdAndUsuarioId(sequencialId, chatId,
                        usuarioId).
                orElseThrow(() -> new MensagemNaoEncontradaUsandoReferencias(sequencialId, chatId, usuarioId));

        if (dto.texto() != null && !dto.texto().isBlank()) {
            mensagemDireta.setTexto(dto.texto());
        }

        mensagemDireta.setArquivoId(dto.arquivoId());
        mensagemDireta.setAtualizadoEm(LocalDateTime.now());

        mensagemDireta = msgRepository.save(mensagemDireta);

        return new MensagemResponseDTO(mensagemDireta.getSequencialId(), mensagemDireta.getTexto(),
                mensagemDireta.getTipoUsuario(), mensagemDireta.getCriadoEm(), mensagemDireta.getArquivoId(),
                mensagemDireta.getUsuarioId());
    }

    @Transactional
    public void excluirMensagem(Long sequencialId, Long chatId, Long usuarioId) {
        MensagemDireta mensagemDireta = msgRepository.findBySequencialIdAndChatIdAndUsuarioId(sequencialId, chatId,
                        usuarioId).orElseThrow(
                                () -> new MensagemNaoEncontradaUsandoReferencias(sequencialId, chatId, usuarioId));
        msgRepository.deleteById(mensagemDireta.getId());
    }

    @Transactional
    public void limparMensagens(Long chatId) {
       msgRepository.deleteAllByChatId(chatId);
    }

    @Transactional
    public MensagemDireta mensagemAutomaticaMatchEvento(ChatCriadoViaMatch dto) {
        Long max = msgRepository.findMaxSequencialIdByChatId(dto.dto().chat().getId());

        MensagemDireta mensagemDireta = msgRepository.save(new MensagemDireta(max + 1L, dto.dto().texto(),
                null, dto.dto().chat().getId(), dto.dto().usuarioId(), dto.dto().tipoUsuario()));

        return msgRepository.save(mensagemDireta);
    }
}