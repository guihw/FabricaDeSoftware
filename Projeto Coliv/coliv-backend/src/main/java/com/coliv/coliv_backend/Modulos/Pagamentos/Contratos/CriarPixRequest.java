package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

import java.math.BigDecimal;

public record CriarPixRequest(

        Long usuarioId,

        String nome,

        String email,

        BigDecimal valor

) {
}