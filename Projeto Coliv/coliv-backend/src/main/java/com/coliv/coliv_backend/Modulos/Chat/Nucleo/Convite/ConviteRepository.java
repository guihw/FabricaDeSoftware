package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConviteRepository extends JpaRepository<Convite, Long> {

    Optional<Convite> findByChatId (Long chatId);
}
