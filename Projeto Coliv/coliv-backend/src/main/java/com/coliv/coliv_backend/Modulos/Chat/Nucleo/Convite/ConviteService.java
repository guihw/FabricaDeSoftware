package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.*;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.IMatchmaking;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.ConviteAceitoEvent;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.ConviteRecebidoEvent;
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
    private IMatchmaking matchmaking;
    @Autowired
    private ApplicationEventPublisher publisher;

    public List<ConviteResponseDTO> listarPorUsuario(Long usuarioId, TipoUsuario tipoUsuario) {

        if (tipoUsuario == TipoUsuario.ANFITRIAO) {
            return conviteRepository.findByAnfitriaoId(usuarioId).stream().map(convite -> new
                    ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                    convite.getCriadoEm(), convite.getMatchId(), convite.getAnfitriaoId(), convite.getColegaId(),
                    convite.getAtualizadoEm())).toList();
        } else {
            return conviteRepository.findByColegaId(usuarioId).stream().map(convite -> new
                    ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                    convite.getCriadoEm(), convite.getMatchId(), convite.getAnfitriaoId(), convite.getColegaId(),
                    convite.getAtualizadoEm())).toList();
        }
    }

    public ConviteResponseDTO buscarConviteRecente(Long matchId) {
        Convite convite = conviteRepository.findTopByMatchIdOrderByIdDesc(matchId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(matchId));

        return new ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                convite.getCriadoEm(), convite.getMatchId(), convite.getAnfitriaoId(), convite.getColegaId(),
                convite.getAtualizadoEm());
    }

    @Transactional
    public ConviteResponseDTO novoConvite(ConviteStatus status, ConviteRequestDTO dto, Long matchId) {
        List<ConviteStatus> statuses = List.of(ConviteStatus.PENDENTE, ConviteStatus.ACEITO);

        if (conviteRepository.existsByMatchIdAndConviteStatusIn(matchId, statuses)) {
            throw new ConviteExistente();
        }

        Convite convite = conviteRepository.save(new Convite.Builder().
                conviteStatus(status).
                criadoEm(LocalDateTime.now()).
                texto(dto.texto()).
                matchId(matchId).
                anfitriaoId(matchmaking.getUserId(matchId, TipoUsuario.ANFITRIAO)).
                colegaId(matchmaking.getUserId(matchId, TipoUsuario.COLEGA)).
                build());

        publisher.publishEvent(new ConviteEnviado(new ConviteInfoDTO(convite.getId(), matchId, convite.getTexto(),
                convite.getCriadoEm(), convite.getAtualizadoEm())
        ));
        publisher.publishEvent(new ConviteRecebidoEvent(convite.getColegaId(), convite.getId()));

        return new ConviteResponseDTO(convite.getId(), convite.getConviteStatus(), convite.getTexto(),
                convite.getCriadoEm(), convite.getAnfitriaoId(), convite.getColegaId(), convite.getMatchId(),
                convite.getAtualizadoEm());
    }

    @Transactional
    public void conviteAceito(Long matchId) {
        Convite convite = conviteRepository.findTopByMatchIdOrderByIdDesc(matchId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(matchId));

        if (convite.getConviteStatus() != ConviteStatus.PENDENTE) {
            throw new ConviteStatusException(convite.getConviteStatus());
        }

        convite.setConviteStatus(ConviteStatus.ACEITO);
        convite.setAtualizadoEm(LocalDateTime.now());
        conviteRepository.save(convite);

        publisher.publishEvent(new ConviteAceito(new ConviteAceitoDTO(convite.getAnfitriaoId(), convite.getColegaId(),
                new ConviteInfoDTO(convite.getId(), matchId, convite.getTexto(), convite.getCriadoEm(),
                        convite.getAtualizadoEm()
                ))));
        publisher.publishEvent(new ConviteAceitoEvent(convite.getAnfitriaoId(), convite.getId()));
    }

    @Transactional
    public void conviteRecusado(Long matchId) {
        Convite convite = conviteRepository.findTopByMatchIdOrderByIdDesc(matchId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(matchId));

        if (convite.getConviteStatus() != ConviteStatus.PENDENTE) {
            throw new ConviteStatusException(convite.getConviteStatus());
        }

        convite.setConviteStatus(ConviteStatus.RECUSADO);
        convite.setAtualizadoEm(LocalDateTime.now());
        conviteRepository.save(convite);
    }

    @Transactional
    public void conviteCancelado(Long matchId) {
        Convite convite = conviteRepository.findTopByMatchIdOrderByIdDesc(matchId).orElseThrow(() -> new
                ConviteNaoEncontradoUsandoReferencia(matchId));

        if (convite.getConviteStatus() == ConviteStatus.ACEITO) {
            throw new ConviteStatusException(convite.getConviteStatus());
        }

        convite.setConviteStatus(ConviteStatus.CANCELADO);
        convite.setAtualizadoEm(LocalDateTime.now());
        conviteRepository.save(convite);
    }
}