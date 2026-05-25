package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferenciasColegaRepository extends JpaRepository<PreferenciasColega, Long> {

    Optional<PreferenciasColega> findByColegaId(Long colegaId);
}