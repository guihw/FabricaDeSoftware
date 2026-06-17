package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {


    @Mock
    private MatchRepository repository;

    @InjectMocks
    private MatchService service;

    @Test
    void criar_deveCriarMatchComStatusPendente() {

        Match match = new Match();
        match.setColegaId(1L);
        match.setAnfitriaoId(2L);
        match.setStatus(StatusMatch.PENDENTE);

        when(repository.save(any(Match.class)))
                .thenReturn(match);

        MatchResponse response =
                service.criar(1L, 2L);

        assertNotNull(response);
        assertEquals(1L, response.colegaId());
        assertEquals(2L, response.anfitriaoId());
        assertEquals(StatusMatch.PENDENTE, response.status());

        verify(repository, times(1))
                .save(any(Match.class));
    }

    @Test
    void buscar_deveRetornarMatchQuandoExistir() {

        Match match = new Match();

        match.setColegaId(1L);
        match.setAnfitriaoId(2L);
        match.setStatus(StatusMatch.PENDENTE);

        match.setId(10L);

        when(repository.findById(10L))
                .thenReturn(Optional.of(match));

        MatchResponse response =
                service.buscar(10L);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(1L, response.colegaId());
        assertEquals(2L, response.anfitriaoId());
    }

    @Test
    void buscar_deveLancarExcecaoQuandoNaoExistir() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                MatchIdNaoEncontrado.class,
                () -> service.buscar(99L)
        );
    }

    @Test
    void cancelar_deveAlterarStatusParaCancelado() {

        Match match = new Match();

        match.setId(10L);
        match.setStatus(StatusMatch.PENDENTE);

        when(repository.findById(10L))
                .thenReturn(Optional.of(match));

        service.cancelar(10L);

        assertEquals(
                StatusMatch.CANCELADO,
                match.getStatus()
        );

        verify(repository)
                .save(match);
    }

    @Test
    void cancelar_deveLancarExcecaoQuandoNaoExistir() {

        when(repository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                MatchIdNaoEncontrado.class,
                () -> service.cancelar(10L)
        );

        verify(repository, never())
                .save(any());
    }


}
