package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Mensagem.MensagemDireta;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MensagemDiretaRepository extends JpaRepository<MensagemDireta, Long> {

    List<MensagemDireta> findByUsuarioIdAndTipoUsuario(Long id, TipoUsuario tipoUsuario);

    List<MensagemDireta> findByChatId(Long chatId);

    List<MensagemDireta> findByChatIdAndUsuarioIdAndTipoUsuario(Long chatId, Long usuarioId, TipoUsuario tipoUsuario);

    List<MensagemDireta> findByChatIdAndTextoContainingIgnoreCase(Long chatId, String texto);

    @Query ("SELECT COALESCE(MAX(md.sequencialId), 0L) FROM MensagemDireta md WHERE md.chatId = :chatId")
    Long findMaxSequencialIdByChatId(@Param("chatId") Long chatId);

    Optional<MensagemDireta> findBySequencialIdAndChatIdAndUsuarioId(Long sequencialId, Long chatId, Long usuarioId);

    void deleteAllByChatId (Long chatId);
}