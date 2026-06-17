package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;

interface ComentarioRepository
        extends JpaRepository<Comentario, Long> {
}