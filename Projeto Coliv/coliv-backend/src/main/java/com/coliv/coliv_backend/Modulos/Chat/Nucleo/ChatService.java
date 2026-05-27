package com.coliv.coliv_backend.Modulos.Chat.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.*;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEvento;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEventoDTO;
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
    private ApplicationEventPublisher publisher;

    public List<ChatResponseDTO> listarChats() {
        return chatRepository.findAll().stream().map(chat ->
                new ChatResponseDTO(chat.getId(), chat.getUsuarioListId())).toList();
    }

    public ChatResponseDTO buscarPorId(Long id) {
        return chatRepository.findById(id).map(chat ->
                new ChatResponseDTO(id, chat.getUsuarioListId())).
                orElseThrow(() -> new ChatIDNaoEncontrado(id));
    }

    public ChatResponseDTO criarChat(ChatRequestDTO dto) {
        Chat novo = new Chat();
        novo.setUsuarioListId(dto.listaUsuarioId());

        novo = chatRepository.save(novo);

        return new ChatResponseDTO(novo.getId(), novo.getUsuarioListId());
    }

    public ChatResponseDTO editarChat(Long id, ChatRequestDTO dto) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new ChatIDNaoEncontrado(id));

        if (dto.listaUsuarioId() != null) {
            chat.setUsuarioListId((dto.listaUsuarioId()));
        }

        chatRepository.save(chat);

        return new ChatResponseDTO(id, dto.listaUsuarioId());
    }

    public void excluir(Long id) {
        chatRepository.findById(id).orElseThrow(() -> new ChatIDNaoEncontrado(id));
        chatRepository.deleteById(id);
    }

    @EventListener
    @Transactional
    void eventoMatch(MatchEvento evento) {
        Chat chat = new Chat();
        List<Long> list = new ArrayList<>();
        list.add(evento.dto().anfitriaoId());
        list.add(evento.dto().colegaId());
        chat.setUsuarioListId(list);

        chat = chatRepository.save(chat);

        switch (evento.dto().iniciador()) {
            case ANFITRIAO -> {
                Mensagem mensagem = new Mensagem.Builder().
                        texto("Olá! Vi que você tem interesse no imóvel. Fico à disposição para conversar!").
                        chat(chat).
                        criadoEm(LocalDateTime.now()).
                        usuarioId(evento.dto().anfitriaoId()).
                        build();

                publisher.publishEvent(new ChatCriadoViaMatch(new
                        MensagemAutomaticaDTO(mensagem.getTexto(), evento.dto().iniciador(), chat.getId(),
                        mensagem.getCriadoEm(), mensagem.getUsuarioId())));
            }

            case COLEGA -> {
                Mensagem mensagem = new Mensagem.Builder().
                        texto("Olá! Gostaria de saber mais sobre o anúncio.").
                        chat(chat).
                        criadoEm(LocalDateTime.now()).
                        usuarioId(evento.dto().colegaId()).
                        build();

                publisher.publishEvent(new ChatCriadoViaMatch(new
                        MensagemAutomaticaDTO(mensagem.getTexto(), evento.dto().iniciador(), chat.getId(),
                        mensagem.getCriadoEm(), mensagem.getUsuarioId())));
            }
        }
    }

    //Não esquecer de remover quando criar a classe de Match
    public void matchEventTeste(MatchEventoDTO dto) {
        publisher.publishEvent(new MatchEvento(dto));
    }
}