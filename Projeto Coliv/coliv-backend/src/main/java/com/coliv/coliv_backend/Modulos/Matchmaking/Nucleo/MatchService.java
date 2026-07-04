package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.*;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.NovoMatchEvent;
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
        return demonstrarInteresse(dto.colegaId(), dto.anfitriaoId(), dto.iniciador());
    }

    // Quando o ANFITRIÃO demonstra interesse em um colega recomendado.
    @Transactional
    public MatchResponse criarAceito(Long colegaId, Long anfitriaoId) {
        return demonstrarInteresse(colegaId, anfitriaoId, TipoUsuario.ANFITRIAO);
    }

    // Registra o interesse de um dos lados (colega ou anfitrião) no outro.
    // - Se já existe match ACEITO para o par: idempotente, apenas retorna (não duplica, não republica eventos).
    // - Se já existe PENDENTE do MESMO lado (ex.: clique repetido/refresh): idempotente, retorna o existente.
    // - Se já existe PENDENTE do lado OPOSTO: os dois demonstraram interesse — completa o match e dispara os eventos.
    // - Caso contrário: cria um novo PENDENTE.
    // Matches CANCELADO são ignorados, permitindo um novo interesse ser registrado após um cancelamento.
    private MatchResponse demonstrarInteresse(Long colegaId, Long anfitriaoId, TipoUsuario iniciador) {

        Optional<Match> existenteOpt = repository
                .findFirstByColegaIdAndAnfitriaoIdAndStatusNot(colegaId, anfitriaoId, StatusMatch.CANCELADO);

        if (existenteOpt.isPresent()) {
            Match match = existenteOpt.get();

            if (match.getStatus() == StatusMatch.ACEITO || match.getIniciador() == iniciador) {
                return toResponse(match);
            }

            match.setStatus(StatusMatch.ACEITO);
            match = repository.save(match);

            publisher.publishEvent(new MatchEvento(
                    new MatchEventoDTO(match.getAnfitriaoId(), match.getColegaId(), match.getIniciador())));
            publisher.publishEvent(new NovoMatchEvent(match.getAnfitriaoId(), match.getColegaId(), match.getId()));

            return toResponse(match);
        }

        Match match = new Match();
        match.setColegaId(colegaId);
        match.setAnfitriaoId(anfitriaoId);
        match.setIniciador(iniciador);
        match.setStatus(StatusMatch.PENDENTE);
        match.setCriadoEm(LocalDateTime.now());
        match = repository.save(match);

        return toResponse(match);
    }

    private MatchResponse toResponse(Match match) {
        return new MatchResponse(match.getId(), match.getColegaId(), match.getAnfitriaoId(), match.getStatus(),
                match.getIniciador());
    }

    @Transactional
    public void aceitar(Long id) {

        Match match = repository.findById(id).orElseThrow(() -> new MatchIdNaoEncontrado(id));

        if (match.getStatus() == StatusMatch.PENDENTE) {
            match.setStatus(StatusMatch.ACEITO);
        }

        publisher.publishEvent(new MatchEvento(new MatchEventoDTO(match.getAnfitriaoId(), match.getColegaId(),
                match.getIniciador())));
        publisher.publishEvent(new NovoMatchEvent(match.getAnfitriaoId(), match.getColegaId(), match.getId()));

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
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<MatchResponse> listarPorColega(Long colegaId) {
        return repository.findByColegaId(colegaId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<MatchResponse> listarPorAnfitriao(Long anfitriaoId) {
        return repository.findByAnfitriaoId(anfitriaoId).stream().map(this::toResponse).toList();
    }

    public MatchResponse buscar(Long id) {

        Match match =
                repository.findById(id)
                        .orElseThrow(() ->
                                new MatchIdNaoEncontrado(id));

        return toResponse(match);
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