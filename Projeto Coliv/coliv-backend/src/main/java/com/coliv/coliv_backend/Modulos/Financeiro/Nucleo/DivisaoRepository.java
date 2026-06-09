package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface DivisaoRepository
        extends JpaRepository<Divisao, Long> {

    List<Divisao> findByDespesaId(Long despesaId);
}