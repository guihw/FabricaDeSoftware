package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConviteRepository extends JpaRepository<Convite, Long> {

    List<Convite> findByAnfitriaoId(Long anfitriaoId);

    List<Convite> findByColegaId(Long colegaId);

    Optional<Convite> findByMatchId(Long matchId);

    Optional<Convite> findTopByMatchIdOrderByIdDesc(Long matchId);

    boolean existsByMatchIdAndConviteStatusIn(Long matchId, List<ConviteStatus> statuses);
}