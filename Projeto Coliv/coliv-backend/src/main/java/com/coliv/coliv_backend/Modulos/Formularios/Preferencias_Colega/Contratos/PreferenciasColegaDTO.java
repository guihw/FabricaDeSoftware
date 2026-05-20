package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.HabitoDeTrabalho;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeLimpeza;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeSociabilidade;

import java.math.BigDecimal;
import java.time.LocalTime;

public record PreferenciasColegaDTO(

        BigDecimal precoMinimo,
        BigDecimal precoMaximo,
        String localizacao,
        LocalTime horarioDeSono,
        NivelDeSociabilidade nivelDeSociabilidade,
        NivelDeLimpeza nivelDeLimpeza,
        HabitoDeTrabalho habitoDeTrabalho,
        boolean aceitaAnimais

) {
}