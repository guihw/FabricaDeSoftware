package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Envelope comum a todos os webhooks v2 da AbacatePay.
 * event ex: "transparent.completed".
 * data é deixado como JsonNode (genérico) porque a AbacatePay recomenda
 * não validar o payload inteiro com schema estrito — campos novos no
 * futuro não devem quebrar este endpoint. Extraímos manualmente só o
 * que precisamos (id do transparent e status).
 */
public record AbacateWebhookV2Payload(
        String id,
        String event,
        Integer apiVersion,
        Boolean devMode,
        JsonNode data
) {
}
