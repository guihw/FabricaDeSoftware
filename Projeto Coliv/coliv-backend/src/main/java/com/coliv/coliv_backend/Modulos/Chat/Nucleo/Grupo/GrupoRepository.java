package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    Optional<Grupo> findByAnfitriaoId(Long id);

    @Query("SELECT g FROM Grupo g JOIN g.membros m WHERE m.usuarioId = :colegaId AND m.tipoUsuario = 'COLEGA'")
    Optional<Grupo> findByColegaId(@Param("colegaId") Long colegaId);
}