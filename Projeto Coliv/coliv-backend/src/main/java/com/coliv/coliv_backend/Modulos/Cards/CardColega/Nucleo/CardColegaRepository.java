package com.coliv.coliv_backend.Modulos.Cards.CardColega.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface CardColegaRepository
        extends JpaRepository<CardColega, Long> {

    Optional<CardColega> findByColegaId(Long colegaId);
}