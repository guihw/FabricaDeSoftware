package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface CardAnfitriaoRepository extends JpaRepository<CardAnfitriao, Long> {

    Optional<CardAnfitriao> findByAnfitriaoId(Long id);
}
