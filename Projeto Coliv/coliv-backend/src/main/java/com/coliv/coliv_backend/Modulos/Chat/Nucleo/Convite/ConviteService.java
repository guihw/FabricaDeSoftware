package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Chat.ChatIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.*;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.Chat;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Chat.ChatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConviteService {

    @Autowired
    private ConviteRepository conviteRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ApplicationEventPublisher publisher;

    @Transactional
    public ConviteInfoDTO novoConvite(ConviteStatus status, ConviteRequestDTO dto, Long chatId) {
         Convite convite = conviteRepository.save(new Convite.Builder().
                conviteStatus(status).
                valorDefinido(dto.valorDefinido()).
                dataInicio(LocalDateTime.now()).
                condicoesIniciais(dto.condicoes()).
                chat(chatRepository.findById(chatId).orElseThrow(() -> new ChatIDNaoEncontrado(chatId))).
                build());

        return new ConviteInfoDTO(convite.getId(), chatId, convite.getValorDefinido(), convite.getCondicoesIniciais(),
                convite.getDataInicio());
    }

    @Transactional
    public void conviteAceito(Long chatId) {
        Convite convite = conviteRepository.findByChatId(chatId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(chatId));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatIDNaoEncontrado(chatId));

        convite.setConviteStatus(ConviteStatus.ACEITO);
        conviteRepository.save(convite);

        publisher.publishEvent(new ConviteAceito(new ConviteAceitoDTO(chat.getAnfitriaoId(), chat.getColegaId(), new
                ConviteInfoDTO(convite.getId(), chatId, convite.getValorDefinido(), convite.getCondicoesIniciais(),
                convite.getDataInicio()))));
    }

    @Transactional
    public void conviteNegado(Long chatId) {
        Convite convite = conviteRepository.findByChatId(chatId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(chatId));

        convite.setConviteStatus(ConviteStatus.NEGADO);
        conviteRepository.save(convite);
    }
}