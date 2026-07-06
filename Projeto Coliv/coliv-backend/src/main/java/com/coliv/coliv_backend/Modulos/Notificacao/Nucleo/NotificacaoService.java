package com.coliv.coliv_backend.Modulos.Notificacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.NotificacaoDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    NotificacaoService(NotificacaoRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void criar(Long usuarioId, TipoNotificacao tipo, String titulo, String mensagem, Long referenciaId) {
        List<Object[]> linhas = entityManager.createNativeQuery(
                "INSERT INTO notificacao (usuario_id, tipo, titulo, mensagem, referencia_id, lida, criado_em) " +
                "VALUES (:usuarioId, :tipo, :titulo, :mensagem, :referenciaId, false, now()) " +
                "ON CONFLICT (usuario_id, tipo, referencia_id) WHERE lida = false DO NOTHING " +
                "RETURNING id, criado_em")
                .setParameter("usuarioId", usuarioId)
                .setParameter("tipo", tipo.name())
                .setParameter("titulo", titulo)
                .setParameter("mensagem", mensagem)
                .setParameter("referenciaId", referenciaId)
                .getResultList();

        if (linhas.isEmpty()) {
            return;
        }

        Object[] linha = linhas.get(0);
        Long id = ((Number) linha[0]).longValue();
        Timestamp criadoEm = (Timestamp) linha[1];

        NotificacaoDTO dto = new NotificacaoDTO(id, usuarioId, tipo, titulo, mensagem, referenciaId, false, criadoEm.toLocalDateTime());
        messagingTemplate.convertAndSend("/topic/notificacoes." + usuarioId, dto);
    }

    public List<NotificacaoDTO> buscarNaoLidas(Long usuarioId) {
        return repository
                .findByUsuarioIdAndLidaFalseOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void marcarComoLida(Long notificacaoId, Long usuarioId) {
        repository.findById(notificacaoId).ifPresent(n -> {
            if (n.getUsuarioId().equals(usuarioId)) {
                n.marcarComoLida();
                repository.save(n);
            }
        });
    }

    private NotificacaoDTO toDTO(Notificacao n) {
        return new NotificacaoDTO(
                n.getId(),
                n.getUsuarioId(),
                n.getTipo(),
                n.getTitulo(),
                n.getMensagem(),
                n.getReferenciaId(),
                n.isLida(),
                n.getCriadoEm()
        );
    }
}
