package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
class MatchService implements IMatchmaking {

    @Autowired
    private MatchRepository repository;
    @Autowired
    private ApplicationEventPublisher publisher;

    @Transactional
    public MatchResponse criar(MatchDTO dto) {

        // Se o anfitrião já demonstrou interesse (PENDENTE com iniciador ANFITRIAO),
        // completa o match e cria o chat em vez de gerar um segundo PENDENTE.
        Optional<Match> pendenteAnfitriaoOpt = repository
                .findByColegaIdAndAnfitriaoIdAndStatus(dto.colegaId(), dto.anfitriaoId(), StatusMatch.PENDENTE);

        if (pendenteAnfitriaoOpt.isPresent()) {
            Match match = pendenteAnfitriaoOpt.get();
            match.setStatus(StatusMatch.ACEITO);
            match = repository.save(match);

            publisher.publishEvent(new MatchEvento(
                    new MatchEventoDTO(dto.anfitriaoId(), dto.colegaId(), match.getIniciador())));

            return new MatchResponse(
                    match.getId(),
                    match.getColegaId(),
                    match.getAnfitriaoId(),
                    match.getStatus()
            );
        }

        Match match = new Match();
        match.setColegaId(dto.colegaId());
        match.setAnfitriaoId(dto.anfitriaoId());
        match.setIniciador(dto.iniciador());
        match.setStatus(StatusMatch.PENDENTE);
        match.setCriadoEm(LocalDateTime.now());

        match = repository.save(match);

        return new MatchResponse(
                match.getId(),
                match.getColegaId(),
                match.getAnfitriaoId(),
                match.getStatus()
        );
    }

    // Quando o ANFITRIÃO demonstra interesse em um colega recomendado.
    // Se o colega já demonstrou interesse (match PENDENTE com iniciador COLEGA),
    // completa o match como ACEITO e cria o chat.
    // Caso contrário, registra apenas o interesse do anfitrião (PENDENTE com iniciador ANFITRIAO).
    @Transactional
    public MatchResponse criarAceito(Long colegaId, Long anfitriaoId) {

        Optional<Match> pendenteOpt = repository
                .findByColegaIdAndAnfitriaoIdAndStatus(colegaId, anfitriaoId, StatusMatch.PENDENTE);

        if (pendenteOpt.isPresent()) {
            // Colega já demonstrou interesse — match completo
            Match match = pendenteOpt.get();
            match.setStatus(StatusMatch.ACEITO);
            match = repository.save(match);

            publisher.publishEvent(new MatchEvento(
                    new MatchEventoDTO(anfitriaoId, colegaId, match.getIniciador())));

            return new MatchResponse(
                    match.getId(),
                    match.getColegaId(),
                    match.getAnfitriaoId(),
                    match.getStatus()
            );
        }

        // Anfitrião vai primeiro — registra interesse sem abrir chat
        Match match = new Match();
        match.setColegaId(colegaId);
        match.setAnfitriaoId(anfitriaoId);
        match.setIniciador(TipoUsuario.ANFITRIAO);
        match.setStatus(StatusMatch.PENDENTE);
        match.setCriadoEm(LocalDateTime.now());
        match = repository.save(match);

        return new MatchResponse(
                match.getId(),
                match.getColegaId(),
                match.getAnfitriaoId(),
                match.getStatus()
        );
    }

    @Transactional
    public void aceitar(Long id) {

        Match match = repository.findById(id).orElseThrow(() -> new MatchIdNaoEncontrado(id));

        if (match.getStatus() == StatusMatch.PENDENTE) {
            match.setStatus(StatusMatch.ACEITO);
        }

        publisher.publishEvent(new MatchEvento(new MatchEventoDTO(match.getAnfitriaoId(), match.getColegaId(),
                match.getIniciador())));

        repository.save(match);
    }

    @Transactional
    public void cancelar(Long id) {

        Match match =
                repository.findById(id)
                        .orElseThrow(() ->
                                new MatchIdNaoEncontrado(id));

        match.setStatus(StatusMatch.CANCELADO);

        repository.save(match);
    }

    @Override
    public List<MatchResponse> listar() {
        return repository.findAll().stream().map(match -> new MatchResponse(match.getId(), match.getColegaId(),
                match.getAnfitriaoId(), match.getStatus())).toList();
    }

    public List<MatchResponse> listarPorColega(Long colegaId) {
        return repository.findByColegaId(colegaId).stream()
                .map(m -> new MatchResponse(m.getId(), m.getColegaId(), m.getAnfitriaoId(), m.getStatus()))
                .toList();
    }

    public List<MatchResponse> listarPorAnfitriao(Long anfitriaoId) {
        return repository.findByAnfitriaoId(anfitriaoId).stream()
                .map(m -> new MatchResponse(m.getId(), m.getColegaId(), m.getAnfitriaoId(), m.getStatus()))
                .toList();
    }

    public MatchResponse buscar(Long id) {

        Match match =
                repository.findById(id)
                        .orElseThrow(() ->
                                new MatchIdNaoEncontrado(id));

        return new MatchResponse(
                match.getId(),
                match.getColegaId(),
                match.getAnfitriaoId(),
                match.getStatus()
        );
    }

    @Override
    public Long getUserId(Long matchId, TipoUsuario tipoUsuario) {
        Match match = repository.findById(matchId).orElseThrow(() -> new MatchIdNaoEncontrado(matchId));

        if (tipoUsuario == TipoUsuario.ANFITRIAO) {
            return match.getAnfitriaoId();
        } else {
            return match.getColegaId();
        }
    }
}