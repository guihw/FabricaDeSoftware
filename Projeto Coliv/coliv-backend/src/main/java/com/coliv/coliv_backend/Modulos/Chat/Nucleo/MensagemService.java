package com.coliv.coliv_backend.Modulos.Chat.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.ChatCriadoViaMatch;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.ChatIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.MensagemRequestDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.MensagemResponseDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
class MensagemService {

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
        return msgRepository.findByChatIdAndUsuarioIdAndTipoUsuario(usuarioId, chatId, tipoUsuario).stream().map(
                mensagem -> new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(),
                        mensagem.getTipoUsuario(), mensagem.getCriadoEm(), mensagem.getArquivoId(),
                        mensagem.getUsuarioId())).toList();
    }

    public MensagemResponseDTO criarMensagem(Long usuarioId, Long chatId, MensagemRequestDTO dto) {
        Long max = msgRepository.findMaxSequencialIdByChatId(chatId);

        Mensagem mensagem = new Mensagem.Builder().
                texto(dto.texto()).
                tipoUsuario(dto.tipoUsuario()).
                chat(chatRepository.findById(chatId).orElseThrow(() -> new ChatIDNaoEncontrado(chatId))).
                sequencialId(max + 1).
                arquivoId(dto.arquivoId()).
                usuarioId(usuarioId).
                criadoEm(LocalDateTime.now()).
                build();

        mensagem = msgRepository.save(mensagem);

        return new MensagemResponseDTO(mensagem.getSequencialId(), mensagem.getTexto(), mensagem.getTipoUsuario(),
                mensagem.getCriadoEm(), mensagem.getArquivoId(), mensagem.getUsuarioId());
    }

    @EventListener
    @Transactional
    public void mensagemAutomaticaEvento(ChatCriadoViaMatch evento) {
        Long max = msgRepository.findMaxSequencialIdByChatId(evento.dto().chatId());

        msgRepository.save(new Mensagem.Builder().
                texto(evento.dto().texto()).
                tipoUsuario(evento.dto().tipoUsuario()).
                chat(chatRepository.findById(evento.dto().chatId()).
                        orElseThrow(() -> new ChatIDNaoEncontrado(evento.dto().chatId()))).
                criadoEm(evento.dto().criadoEm()).
                sequencialId(max + 1).
                usuarioId(evento.dto().usuarioId()).
                build());
    }


}