package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacateWebhookPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recebe notificações da AbacatePay sobre o status dos pagamentos PIX.
 *
 * Segurança: o fluxo PIX QR Code (v1) da AbacatePay é validado por um
 * secret enviado na query string da URL do webhook, não por assinatura
 * HMAC no header (isso é só no fluxo de Checkout v2). Configure no
 * dashboard da AbacatePay a URL:
 *   https://seusite.com/pagamentos/webhook?webhookSecret=SEU_SECRET
 */
@RestController
@RequestMapping("/pagamentos")
public class AbacatePayWebhookController {

    private final PagamentoService pagamentoService;
    private final AbacatePayProperties properties;

    public AbacatePayWebhookController(
            PagamentoService pagamentoService,
            AbacatePayProperties properties
    ) {
        this.pagamentoService = pagamentoService;
        this.properties = properties;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberWebhook(
            @RequestParam(name = "webhookSecret", required = false) String webhookSecret,
            @RequestBody AbacateWebhookPayload payload
    ) {
        if (webhookSecret == null || !webhookSecret.equals(properties.getWebhookSecret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (payload == null || payload.data() == null || payload.data().id() == null) {
            // Conforme recomendação da AbacatePay, não validamos o payload inteiro
            // (campos extras futuros não devem quebrar o endpoint), mas sem o id
            // do PIX não há o que processar.
            return ResponseEntity.ok().build();
        }

        StatusPagamento novoStatus = mapearStatus(payload.data().status());
        pagamentoService.atualizarStatusPorBillingId(payload.data().id(), novoStatus);

        return ResponseEntity.ok().build();
    }

    private StatusPagamento mapearStatus(String statusAbacatePay) {
        if (statusAbacatePay == null) {
            return StatusPagamento.PENDENTE;
        }

        return switch (statusAbacatePay) {
            case "PAID" -> StatusPagamento.PAGO;
            case "EXPIRED" -> StatusPagamento.EXPIRADO;
            case "CANCELLED" -> StatusPagamento.CANCELADO;
            default -> StatusPagamento.PENDENTE;
        };
    }
}
