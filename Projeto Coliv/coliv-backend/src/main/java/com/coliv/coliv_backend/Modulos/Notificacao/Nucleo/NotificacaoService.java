package com.coliv.coliv_backend.Modulos.Notificacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.NotificacaoDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    NotificacaoService(NotificacaoRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void criar(Long usuarioId, TipoNotificacao tipo, String titulo, String mensagem, Long referenciaId) {
        Notificacao notificacao = new Notificacao(usuarioId, tipo, titulo, mensagem, referenciaId);

        try {
            notificacao = repository.save(notificacao);
        } catch (DataIntegrityViolationException e) {
            return;
        }

        NotificacaoDTO dto = toDTO(notificacao);
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
