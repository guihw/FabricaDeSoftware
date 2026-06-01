package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.HabitoDeTrabalho;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeLimpeza;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeSociabilidade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalTime;

public record PreferenciasColegaDTO(

        @NotNull
        @Positive
        BigDecimal precoMinimo,

        @NotNull
        @Positive
        BigDecimal precoMaximo,

        @NotBlank
        String localizacao,

        @NotNull
        LocalTime horarioDeSono,

        @NotNull
        NivelDeSociabilidade nivelDeSociabilidade,

        @NotNull
        NivelDeLimpeza nivelDeLimpeza,

        @NotNull
        HabitoDeTrabalho habitoDeTrabalho,

        boolean aceitaAnimais

) {
}