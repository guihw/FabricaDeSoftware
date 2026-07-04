package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AbacatePayProperties.class)
public class AbacatePayConfig {

    @Bean
    RestClient restClient(
            RestClient.Builder builder,
            AbacatePayProperties properties
    ) {
        return builder
                .baseUrl(properties.getUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
