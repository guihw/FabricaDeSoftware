package com.coliv.coliv_backend.Modulos.Antecedentes.Contratos;

import java.time.LocalDateTime;

public record ResultadoAntecedentesDTO(
        String protocolo,
        String status,
        String nomeConsultado,
        String cpfConsultado,
        String tipoUsuario,
        LocalDateTime dataVerificacao,
        String orgaoEmissor,
        String detalhes
) {}
