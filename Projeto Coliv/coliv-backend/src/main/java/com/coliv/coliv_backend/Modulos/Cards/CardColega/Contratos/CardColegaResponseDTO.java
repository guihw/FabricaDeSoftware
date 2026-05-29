package com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.HabitoDeTrabalho;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeLimpeza;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeSociabilidade;

import java.math.BigDecimal;
import java.time.LocalTime;

public record CardColegaResponseDTO(

        String nome,
        String email,
        String descricao,
        Float classificacao,

        String localizacao,

        BigDecimal precoMinimo,
        BigDecimal precoMaximo,

        LocalTime horarioDeSono,

        NivelDeSociabilidade nivelDeSociabilidade,
        NivelDeLimpeza nivelDeLimpeza,
        HabitoDeTrabalho habitoDeTrabalho,

        boolean aceitaAnimais

) {
}