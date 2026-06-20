package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
class MatchService {

    @Autowired
    private MatchRepository repository;

    public MatchResponse criar(
            Long colegaId,
            Long anfitriaoId
    ) {

        Match match = new Match();

        match.setColegaId(colegaId);
        match.setAnfitriaoId(anfitriaoId);
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

    // Quando o ANFITRIÃO aceita um colega recomendado, o match já nasce ACEITO.
    public MatchResponse criarAceito(
            Long colegaId,
            Long anfitriaoId
    ) {

        Match match = new Match();

        match.setColegaId(colegaId);
        match.setAnfitriaoId(anfitriaoId);
        match.setStatus(StatusMatch.ACEITO);
        match.setCriadoEm(LocalDateTime.now());

        match = repository.save(match);

        return new MatchResponse(
                match.getId(),
                match.getColegaId(),
                match.getAnfitriaoId(),
                match.getStatus()
        );
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

    public void cancelar(Long id) {

        Match match =
                repository.findById(id)
                        .orElseThrow(() ->
                                new MatchIdNaoEncontrado(id));

        match.setStatus(StatusMatch.CANCELADO);

        repository.save(match);
    }
}