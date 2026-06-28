package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

/**
 * Payload enviado pela AbacatePay no webhook do fluxo PIX QR Code (v1).
 * Evento típico: "billing.paid".
 *
 * Atenção: a AbacatePay recomenda NÃO validar o payload inteiro (ex: com Zod
 * no caso deles, ou Jackson estrito no nosso caso) para não quebrar o
 * endpoint se eles adicionarem campos novos no futuro. Por isso usamos
 * apenas os campos que realmente precisamos.
 */
public record AbacateWebhookPayload(
        String event,
        AbacatePixData data
) {
}
