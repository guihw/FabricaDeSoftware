package com.coliv.coliv_backend.Modulos.Security.Nucleo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public AuthenticationManager authenticationManager(
            AutenticacaoUserDetailsService authenticationUserDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(authenticationUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                // 1. ADICIONADO: Ativa o CORS vinculando ao Bean de configuração abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/ws-connect/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        // Cadastro e login são públicos
                        .requestMatchers(HttpMethod.POST, "/usuarios/anfitriao/novo", "/usuarios/colega/novo", "/auth/login").permitAll()
                        .requestMatchers("/validacao/cpf/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pagamentos/webhook").permitAll()

                        // Exclusivo de anfitrião
                        .requestMatchers("/formularios/preferencias-anfitriao/**",
                                "/formularios/dados-imovel/**",
                                "/cards/anfitriao/**",
                                "/recomendacoes/feed/anfitriao/**").hasRole("ANFITRIAO")

                        // Exclusivo de colega
                        .requestMatchers("/recomendacoes/feed/colega/**").hasRole("COLEGA")

                        // Demais rotas exigem apenas estar autenticado (anfitrião ou colega)

                        .requestMatchers("/matches/**").permitAll()
                        .requestMatchers("/chat/**").permitAll()
                        .requestMatchers("/chat/convite/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. ADICIONADO: Configuração explícita de origens aceitas (Local + Vercel)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "https://fabrica-de-software-eight.vercel.app", // Produção Vercel
                "http://localhost:4200",                        // Frontend Angular Local
                "http://localhost",                             // Mobile Local
                "capacitor://localhost",                        // Capacitor Mobile
                "http://10.0.2.2:8080"                          // Emulador Android
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder encoder () {
        return new BCryptPasswordEncoder();
    }
}