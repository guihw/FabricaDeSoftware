package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacateWebhookV2Payload;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Recebe notificações da AbacatePay (Webhooks v2 - Checkout Transparente).
 *
 * Segurança em duas camadas, como recomendado pela AbacatePay:
 * 1. Secret na query string (?webhookSecret=...), definido por nós ao
 *    cadastrar o webhook no dashboard.
 * 2. Assinatura HMAC-SHA256 do corpo, usando a chave PÚBLICA fixa da
 *    AbacatePay, enviada no header "X-Webhook-Signature".
 *
 * Cadastre no dashboard da AbacatePay a URL:
 *   https://seusite.com/pagamentos/webhook?webhookSecret=SEU_SECRET
 */
@RestController
@RequestMapping("/pagamentos")
public class AbacatePayWebhookController {

    // Chave pública fixa da AbacatePay para validação de assinatura HMAC (v2).
    // Não é segredo nosso - é documentada publicamente pela AbacatePay.
    private static final String ABACATEPAY_PUBLIC_KEY =
            "t9dXRhHHo3yDEj5pVDYz0frf7q6bMKyMRmxxCPIPp3RCplBfXRxqlC6ZpiWmOqj4L63qEaeUOtrCI8P0VMUgo6iIga2ri9ogaHFs0WIIywSMg0q7RmBfybe1E5XJcfC4IW3alNqym0tXoAKkzvfEjZxV6bE0oG2zJrNNYmUCKZyV0KZ3JS8Votf9EAWWYdiDkMkpbMdPggfh1EqHlVkMiTady6jOR3hyzGEHrIz2Ret0xHKMbiqkr9HS1JhNHDX9";

    private final PagamentoService pagamentoService;
    private final AbacatePayProperties properties;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

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
            @RequestHeader(name = "X-Webhook-Signature", required = false) String assinatura,
            @RequestBody String corpoBruto
    ) {
        if (webhookSecret == null || !webhookSecret.equals(properties.getWebhookSecret())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (assinatura == null || !assinaturaValida(corpoBruto, assinatura)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AbacateWebhookV2Payload payload;
        try {
            payload = objectMapper.readValue(corpoBruto, AbacateWebhookV2Payload.class);
        } catch (Exception e) {
            // Payload em formato inesperado: não falhar com 500, só ignorar.
            return ResponseEntity.ok().build();
        }

        if (payload.data() == null) {
            return ResponseEntity.ok().build();
        }

        // Conforme recomendação da AbacatePay, não validamos o payload inteiro
        // com schema estrito - extraímos só os campos que precisamos.
        JsonNode transparent = payload.data().get("transparent");
        if (transparent == null) {
            return ResponseEntity.ok().build();
        }

        String billingId = textOrNull(transparent.get("id"));
        String statusAbacatePay = textOrNull(transparent.get("status"));

        if (billingId == null) {
            return ResponseEntity.ok().build();
        }

        StatusPagamento novoStatus = mapearStatus(statusAbacatePay);
        pagamentoService.atualizarStatusPorBillingId(billingId, novoStatus);

        return ResponseEntity.ok().build();
    }

    private boolean assinaturaValida(String corpoBruto, String assinaturaRecebida) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    ABACATEPAY_PUBLIC_KEY.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));
            byte[] hash = mac.doFinal(corpoBruto.getBytes(StandardCharsets.UTF_8));
            String assinaturaEsperada = Base64.getEncoder().encodeToString(hash);

            byte[] a = assinaturaEsperada.getBytes(StandardCharsets.UTF_8);
            byte[] b = assinaturaRecebida.getBytes(StandardCharsets.UTF_8);
            return a.length == b.length && MessageDigest.isEqual(a, b);
        } catch (Exception e) {
            return false;
        }
    }

    private String textOrNull(JsonNode node) {
        return (node == null || node.isNull()) ? null : node.asText();
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