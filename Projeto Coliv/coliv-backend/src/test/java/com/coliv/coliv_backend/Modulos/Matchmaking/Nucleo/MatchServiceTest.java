package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchDTO;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchEvento;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.NovoMatchEvent;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {


    @Mock
    private MatchRepository repository;

    @Mock
    private ApplicationEventPublisher publisher;

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
                service.criar(new MatchDTO(TipoUsuario.COLEGA, 1L, 2L));

        assertNotNull(response);
        assertEquals(1L, response.colegaId());
        assertEquals(2L, response.anfitriaoId());
        assertEquals(StatusMatch.PENDENTE, response.status());

        verify(repository, times(1))
                .save(any(Match.class));
    }

    @Test
    void criar_quandoJaAceito_eIdempotenteENaoDuplica() {

        Match matchExistente = new Match();
        matchExistente.setId(5L);
        matchExistente.setColegaId(1L);
        matchExistente.setAnfitriaoId(2L);
        matchExistente.setIniciador(TipoUsuario.ANFITRIAO);
        matchExistente.setStatus(StatusMatch.ACEITO);

        when(repository.findFirstByColegaIdAndAnfitriaoIdAndStatusNot(1L, 2L, StatusMatch.CANCELADO))
                .thenReturn(Optional.of(matchExistente));

        MatchResponse response = service.criar(new MatchDTO(TipoUsuario.COLEGA, 1L, 2L));

        assertEquals(StatusMatch.ACEITO, response.status());
        assertEquals(5L, response.id());
        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void criar_quandoPendenteDoMesmoIniciador_eIdempotenteENaoDuplica() {

        // Colega já demonstrou interesse antes (ex.: clicou, deu refresh e clicou de novo)
        Match matchExistente = new Match();
        matchExistente.setId(7L);
        matchExistente.setColegaId(1L);
        matchExistente.setAnfitriaoId(2L);
        matchExistente.setIniciador(TipoUsuario.COLEGA);
        matchExistente.setStatus(StatusMatch.PENDENTE);

        when(repository.findFirstByColegaIdAndAnfitriaoIdAndStatusNot(1L, 2L, StatusMatch.CANCELADO))
                .thenReturn(Optional.of(matchExistente));

        MatchResponse response = service.criar(new MatchDTO(TipoUsuario.COLEGA, 1L, 2L));

        assertEquals(StatusMatch.PENDENTE, response.status());
        assertEquals(7L, response.id());
        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void criar_quandoPendenteDoOutroIniciador_completaOMatch() {

        // Anfitrião já havia demonstrado interesse; colega curte de volta
        Match matchExistente = new Match();
        matchExistente.setId(9L);
        matchExistente.setColegaId(1L);
        matchExistente.setAnfitriaoId(2L);
        matchExistente.setIniciador(TipoUsuario.ANFITRIAO);
        matchExistente.setStatus(StatusMatch.PENDENTE);

        when(repository.findFirstByColegaIdAndAnfitriaoIdAndStatusNot(1L, 2L, StatusMatch.CANCELADO))
                .thenReturn(Optional.of(matchExistente));
        when(repository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        MatchResponse response = service.criar(new MatchDTO(TipoUsuario.COLEGA, 1L, 2L));

        assertEquals(StatusMatch.ACEITO, response.status());
        verify(repository, times(1)).save(any(Match.class));
        verify(publisher, times(1)).publishEvent(any(MatchEvento.class));
        verify(publisher, times(1)).publishEvent(any(NovoMatchEvent.class));
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
