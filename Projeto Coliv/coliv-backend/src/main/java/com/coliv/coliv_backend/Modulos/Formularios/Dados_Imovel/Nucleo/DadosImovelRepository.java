package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface DadosImovelRepository extends JpaRepository<DadosImovel, Long> {

    Optional<DadosImovel> findByAnfitriaoId(Long id);
}
