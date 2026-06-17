package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByColegaId(Long colegaId);

    List<Match> findByAnfitriaoId(Long anfitriaoId);
}