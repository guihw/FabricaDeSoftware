package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PreferenciasAnfitriaoRepository extends JpaRepository<PreferenciasAnfitriao, Long> {

    Optional<PreferenciasAnfitriao> findByAnfitriaoId(Long id);
}
