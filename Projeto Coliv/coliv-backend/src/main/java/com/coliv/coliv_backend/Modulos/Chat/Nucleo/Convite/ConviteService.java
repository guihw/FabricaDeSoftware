package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.*;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.ChatRepository;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConviteService {

    @Autowired
    private ConviteRepository conviteRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ApplicationEventPublisher publisher;

    public List<ConviteResponseDTO> listarPorUsuario(Long usuarioId, TipoUsuario tipoUsuario) {

        if (tipoUsuario == TipoUsuario.ANFITRIAO) {
            return conviteRepository.findByAnfitriaoId(usuarioId).stream().map(convite -> new
                    ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                    convite.getCriadoEm(), convite.getAnfitriaoId(), convite.getColegaId(), convite.getChat().getId(),
                    convite.getAtualizadoEm())).toList();
        } else {
            return conviteRepository.findByColegaId(usuarioId).stream().map(convite -> new
                    ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                    convite.getCriadoEm(), convite.getAnfitriaoId(), convite.getColegaId(), convite.getChat().getId(),
                    convite.getAtualizadoEm())).toList();
        }
    }

    public ConviteResponseDTO buscarConviteRecente(Long chatId) {
        Convite convite = conviteRepository.findTopByChatIdOrderByIdDesc(chatId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(chatId));

        return new ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                convite.getCriadoEm(), convite.getAnfitriaoId(), convite.getColegaId(), convite.getChat().getId(),
                convite.getAtualizadoEm());
    }

    @Transactional
    public ConviteResponseDTO novoConvite(ConviteStatus status, ConviteRequestDTO dto, Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatIDNaoEncontrado(chatId));

        if (conviteRepository.existsByChatIdAndConviteStatusIn(chatId, new ArrayList<>(){{
            add(ConviteStatus.PENDENTE);
            add(ConviteStatus.ACEITO);
        }}
        )) {
            throw new ConviteExistente();
        }

        Convite convite = conviteRepository.save(new Convite.Builder().
                conviteStatus(status).
                criadoEm(LocalDateTime.now()).
                texto(dto.texto()).
                chat(chat).
                anfitriaoId(chat.getAnfitriaoId()).
                colegaId(chat.getColegaId()).
                build());

        publisher.publishEvent(new ConviteEnviado(new ConviteInfoDTO(convite.getId(), chatId, convite.getTexto(),
                convite.getCriadoEm(), convite.getAtualizadoEm())
        ));

        return new ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                convite.getCriadoEm(), convite.getAnfitriaoId(), convite.getColegaId(), convite.getChat().getId(),
                convite.getAtualizadoEm());
    }

    @Transactional
    public void conviteAceito(Long chatId) {
        Convite convite = conviteRepository.findByChatId(chatId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(chatId));

        if (convite.getConviteStatus() != ConviteStatus.PENDENTE) {
            throw new ConviteStatusException(convite.getConviteStatus());
        }

        convite.setConviteStatus(ConviteStatus.ACEITO);
        convite.setAtualizadoEm(LocalDateTime.now());
        conviteRepository.save(convite);

        publisher.publishEvent(new ConviteAceito(new ConviteAceitoDTO(convite.getAnfitriaoId(), convite.getColegaId(),
                new ConviteInfoDTO(convite.getId(), chatId, convite.getTexto(), convite.getCriadoEm(),
                        convite.getAtualizadoEm()
                ))));
    }

    @Transactional
    public void conviteRecusado(Long chatId) {
        Convite convite = conviteRepository.findByChatId(chatId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(chatId));

        if (convite.getConviteStatus() != ConviteStatus.PENDENTE) {
            throw new ConviteStatusException(convite.getConviteStatus());
        }

        convite.setConviteStatus(ConviteStatus.RECUSADO);
        convite.setAtualizadoEm(LocalDateTime.now());
        conviteRepository.save(convite);
    }

    @Transactional
    public void conviteCancelado(Long chatId) {
        Convite convite = conviteRepository.findByChatId(chatId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(chatId));

        if (convite.getConviteStatus() == ConviteStatus.ACEITO) {
            throw new ConviteStatusException(convite.getConviteStatus());
        }

        convite.setConviteStatus(ConviteStatus.CANCELADO);
        convite.setAtualizadoEm(LocalDateTime.now());
        conviteRepository.save(convite);
    }
}