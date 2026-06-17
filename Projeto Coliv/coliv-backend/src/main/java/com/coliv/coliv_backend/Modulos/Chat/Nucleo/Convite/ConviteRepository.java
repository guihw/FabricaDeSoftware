package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConviteRepository extends JpaRepository<Convite, Long> {

    List<Convite> findByAnfitriaoId(Long anfitriaoId);

    List<Convite> findByColegaId(Long colegaId);

    Optional<Convite> findByChatId(Long chatId);

    Optional<Convite> findTopByChatIdOrderByIdDesc(Long chatId);

    boolean existsByChatIdAndConviteStatusIn(Long chatId, List<ConviteStatus> statuses);
}