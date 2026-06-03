package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class MatchService implements IMatchmaking {

    private final MatchRepository repository;

    MatchService(MatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MatchResponse> listar() {

        return repository.findAll()
                .stream()
                .map(match -> new MatchResponse(
                        match.getId(),
                        match.getIniciador(),
                        match.getColegaId(),
                        match.getAnfitriaoId()
                ))
                .toList();
    }

    @Override
    public MatchResponse buscar(Long id) {

        Match match = repository.findById(id)
                .orElseThrow(() ->
                        new MatchIdNaoEncontrado(id));

        return new MatchResponse(
                match.getId(),
                match.getIniciador(),
                match.getColegaId(),
                match.getAnfitriaoId()
        );
    }

    @EventListener
    public void criarMatch(MatchEvent evento) {

        Match match = new Match();

        match.setIniciador(evento.iniciador());
        match.setColegaId(evento.colegaId());
        match.setAnfitriaoId(evento.anfitriaoId());

        repository.save(match);
    }

    @EventListener
    public void cancelarMatch(MatchCancelado evento) {

        Match match = repository
                .findByColegaIdAndAnfitriaoId(
                        evento.colegaId(),
                        evento.anfitriaoId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Match não encontrado"));

        repository.delete(match);
    }
}