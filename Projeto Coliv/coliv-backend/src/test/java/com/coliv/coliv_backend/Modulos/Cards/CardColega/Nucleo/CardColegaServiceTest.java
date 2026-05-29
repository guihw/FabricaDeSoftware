package com.coliv.coliv_backend.Modulos.Cards.CardColega.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos.CardColegaNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.HabitoDeTrabalho;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeLimpeza;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo.NivelDeSociabilidade;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.ColegaResponse;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.ColegaExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.UsuarioColegaCriado;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.IPreferenciasColega;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardColegaServiceTest {

    @Mock
    private CardColegaRepository repository;

    @Mock
    private IColega iColega;

    @Mock
    private IPreferenciasColega iPreferenciasColega;

    @InjectMocks
    private CardColegaService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarCardCompleto() {

        Long colegaId = 1L;

        ColegaResponse colega =
                new ColegaResponse(
                        1L,
                        "Fernando",
                        "fernando@email.com",
                        "Dev Java",
                        5.0f,
                        10L
                );

        PreferenciasColegaResponse preferencias =
                new PreferenciasColegaResponse(
                        1L,
                        new BigDecimal("500"),
                        new BigDecimal("1200"),
                        "Maringá",
                        LocalTime.of(22, 0),
                        NivelDeSociabilidade.MODERADO,
                        NivelDeLimpeza.ALTO,
                        HabitoDeTrabalho.HOME_OFFICE,
                        true
                );

        CardColega card = new CardColega();
        card.setColegaId(colegaId);
        card.setClassificacao(4.5F);

        when(iColega.getColega(colegaId))
                .thenReturn(colega);

        when(iPreferenciasColega.getPreferenciasColega(colegaId))
                .thenReturn(preferencias);

        when(repository.findByColegaId(colegaId))
                .thenReturn(Optional.of(card));

        var resultado = service.getCardCompleteInfo(colegaId);

        assertEquals("Fernando", resultado.nome());
        assertEquals("fernando@email.com", resultado.email());
        assertEquals("Dev Java", resultado.descricao());
        assertEquals("Maringá", resultado.localizacao());
    }

    @Test
    void deveLancarErroQuandoCardNaoExistir() {

        Long colegaId = 99L;

        when(repository.findByColegaId(colegaId))
                .thenReturn(Optional.empty());

        assertThrows(
                CardColegaNaoEncontradoUsandoReferencia.class,
                () -> service.getCardCompleteInfo(colegaId)
        );
    }

    @Test
    void deveCriarCardQuandoEventoForRecebido() {

        UsuarioColegaCriado evento =
                new UsuarioColegaCriado(1L);

        service.eventoColegaCriado(evento);

        verify(repository, times(1))
                .save(any(CardColega.class));
    }

    @Test
    void deveExcluirCardQuandoEventoForRecebido() {

        CardColega card = new CardColega();

        card.setId(1L);
        card.setColegaId(10L);

        when(repository.findByColegaId(10L))
                .thenReturn(Optional.of(card));

        service.eventoColegaExcluido(
                new ColegaExcluido(10L)
        );

        verify(repository, times(1))
                .deleteById(1L);
    }
}