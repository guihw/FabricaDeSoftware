package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

interface DespesaRepository
        extends JpaRepository<Despesa, Long> {
}