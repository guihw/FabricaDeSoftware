package com.coliv.coliv_backend.Modulos.Chat.Nucleo;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    List<Mensagem> findByUsuarioIdAndTipoUsuario(Long id, TipoUsuario tipoUsuario);

    List<Mensagem> findByChatId(Long chatId);

    List<Mensagem> findByChatIdAndUsuarioIdAndTipoUsuario(Long chatId, Long usuarioId, TipoUsuario tipoUsuario);

    @Query ("SELECT COALESCE(MAX(m.sequencialId), 0L) FROM Mensagem m WHERE m.chat.id = :chatId")
    Long findMaxSequencialIdByChatId(@Param("chatId") Long chatId);
}
