package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AbacatePayConfig {

    @Value("${abacatepay.url}")
    private String url;

    @Value("${abacatepay.api-key}")
    private String apiKey;

    @Value("${abacatepay.webhook-secret}")
    private String webhookSecret;

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}