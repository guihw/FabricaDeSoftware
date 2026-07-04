package com.coliv.coliv_backend.Modulos.Notificacao.Nucleo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(Long usuarioId);

    boolean existsByChaveIdempotencia(String chaveIdempotencia);
}
