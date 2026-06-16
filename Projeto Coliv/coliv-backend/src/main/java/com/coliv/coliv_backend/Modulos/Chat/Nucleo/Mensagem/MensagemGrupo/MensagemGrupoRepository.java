package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemGrupo;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MensagemGrupoRepository extends JpaRepository<MensagemGrupo, Long> {

    List<MensagemGrupo> findByGrupoId(Long grupoId);

    List<MensagemGrupo> findByUsuarioIdAndTipoUsuario(Long usuarioId, TipoUsuario tipo);

    List<MensagemGrupo> findByGrupoIdAndUsuarioIdAndTipoUsuario(Long grupoId, Long usuarioId, TipoUsuario tipo);

    List<MensagemGrupo> findByGrupoIdAndTextoContainingIgnoreCase(Long grupoId, String texto);

    @Query("select coalesce(max(mg.sequencialId), 0L) from MensagemGrupo mg where mg.grupoId = :grupoId")
    Long findMaxSequence(@Param("grupoId") Long grupoId);

    Optional<MensagemGrupo> findBySequencialIdAndGrupoIdAndUsuarioId(Long id, Long grupoId, Long usuarioId);
}