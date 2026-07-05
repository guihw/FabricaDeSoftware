package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface MembroRepository extends JpaRepository<Membro, Long> {

    Optional<Membro> findByGrupoIdAndUsuarioId(Long grupoId, Long usuarioId);
}
