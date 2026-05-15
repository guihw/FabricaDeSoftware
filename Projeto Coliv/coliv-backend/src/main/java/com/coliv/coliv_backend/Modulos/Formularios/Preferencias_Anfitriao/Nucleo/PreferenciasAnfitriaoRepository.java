package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

interface PreferenciasAnfitriaoRepository extends JpaRepository<PreferenciasAnfitriao, Long> {

    PreferenciasAnfitriao findByAnfitriaoId(Long id);
}
