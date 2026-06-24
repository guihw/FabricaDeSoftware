package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.*;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Mensagem.MensagemAutomaticaMatchDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta.MensagemDireta;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta.MensagemDiretaService;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEvento;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEventoDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MensagemDiretaService msgService;
    @Autowired
    private ApplicationEventPublisher publisher;

    public List<ChatResponseDTO> listarChats() {
        return chatRepository.findAll().stream().map(chat ->
                new ChatResponseDTO(chat.getId(), chat.getAnfitriaoId(), chat.getColegaId())).toList();
    }

    public List<ChatResponseDTO> listarChatsPorUsuario(Long usuarioId, TipoUsuario tipoUsuario) {
        if (tipoUsuario == TipoUsuario.ANFITRIAO) {
            return chatRepository.findByAnfitriaoId(usuarioId).stream().map(chat -> new
                    ChatResponseDTO(chat.getId(), chat.getAnfitriaoId(), chat.getColegaId())).toList();
        } else {
            return chatRepository.findByColegaId(usuarioId).stream().map(chat -> new
                    ChatResponseDTO(chat.getId(), chat.getAnfitriaoId(), chat.getColegaId())).toList();
        }
    }

    public ChatResponseDTO buscarPorId(Long id) {
        return chatRepository.findById(id).map(chat ->
                new ChatResponseDTO(id, chat.getAnfitriaoId(), chat.getColegaId())).orElseThrow(() -> new
                ChatIDNaoEncontrado(id));
    }

    @Transactional
    public void excluir(Long id) {
        chatRepository.findById(id).orElseThrow(() -> new ChatIDNaoEncontrado(id));
        chatRepository.deleteById(id);
    }

    @EventListener
    @Transactional
    void eventoMatch(MatchEvento evento) {
        Chat chat = new Chat.Builder().
                anfitriaoId(evento.dto().anfitriaoId()).
                colegaId(evento.dto().colegaId()).
                build();

        chat = chatRepository.save(chat);

        switch (evento.dto().iniciador()) {
            case ANFITRIAO -> {
                String txt = "Olá! Vi que você tem interesse no imóvel. Fico à disposição para conversar!";

                MensagemDireta mensagemDireta = msgService.mensagemAutomaticaMatchEvento(new ChatCriadoViaMatch(new
                        MensagemAutomaticaMatchDTO(txt, evento.dto().iniciador(), chat,
                        LocalDateTime.now(), evento.dto().anfitriaoId())));

                chat.setMensagens(new ArrayList<>(){{add(mensagemDireta);}});
                chatRepository.save(chat);
            }

            case COLEGA -> {
                String txt = "Olá! Gostaria de saber mais sobre o anúncio.";

                MensagemDireta mensagemDireta = msgService.mensagemAutomaticaMatchEvento(new ChatCriadoViaMatch(new
                        MensagemAutomaticaMatchDTO(txt, evento.dto().iniciador(), chat,
                        LocalDateTime.now(), evento.dto().colegaId())));

                chat.setMensagens(new ArrayList<>(){{add(mensagemDireta);}});
                chatRepository.save(chat);
            }
        }
    }
}