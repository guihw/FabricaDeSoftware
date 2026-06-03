package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface MatchRepository
        extends JpaRepository<Match, Long> {

    Optional<Match> findByColegaIdAndAnfitriaoId(
            Long colegaId,
            Long anfitriaoId
    );
}