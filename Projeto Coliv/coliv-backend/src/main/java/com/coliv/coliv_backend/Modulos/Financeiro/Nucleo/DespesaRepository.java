package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface DespesaRepository
        extends JpaRepository<Despesa, Long> {

    List<Despesa> findAllByAnfitriaoId(Long anfitriaoId);
}