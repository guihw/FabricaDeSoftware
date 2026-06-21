package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Convite;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.*;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.IMatchmaking;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConviteServiceTest {

    @Mock
    private ConviteRepository conviteRepository;
    @Mock
    private IMatchmaking matchmaking;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private ConviteService conviteService;

    Long id = 1L;

    Convite convite = new Convite.Builder().
            id(id).
            texto("Teste").
            matchId(id).
            anfitriaoId(id).
            colegaId(id).
            build();

    @Test
    void buscarConviteRecentePositivo() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));

        ConviteResponseDTO retorno = conviteService.buscarConviteRecente(id);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);

        assertThat(retorno).isNotNull();
        assertThat(retorno.id()).isEqualTo(convite.getId());
    }

    @Test
    void buscarConviteRecenteNegativo() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.buscarConviteRecente(id)).
                isInstanceOf(ConviteNaoEncontradoUsandoReferencia.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
    }

    @Test
    void novoConvitePositivo() {
        List<ConviteStatus> statuses = List.of(ConviteStatus.PENDENTE, ConviteStatus.ACEITO);

        when(conviteRepository.existsByMatchIdAndConviteStatusIn(id, statuses)).thenReturn(false);
        when(conviteRepository.save(any(Convite.class))).thenReturn(convite);
        when(matchmaking.getUserId(any(Long.class), any(TipoUsuario.class))).thenReturn(id).thenReturn(id);

        ConviteResponseDTO retorno = conviteService.novoConvite(ConviteStatus.PENDENTE, new
                ConviteRequestDTO("Teste"), id);
        verify(conviteRepository, times(1)).existsByMatchIdAndConviteStatusIn(id, statuses);
        verify(conviteRepository, times(1)).save(any(Convite.class));
        verify(matchmaking, times(2)).getUserId(any(Long.class), any(TipoUsuario.class));
        verify(publisher, times(1)).publishEvent(any(ConviteEnviado.class));

        assertThat(retorno).isNotNull();
    }

    @Test
    void novoConviteNegativo() {
        List<ConviteStatus> statuses = List.of(ConviteStatus.PENDENTE, ConviteStatus.ACEITO);

        when(conviteRepository.existsByMatchIdAndConviteStatusIn(id, statuses)).thenReturn(true);

        assertThatThrownBy(() ->  conviteService.novoConvite(ConviteStatus.PENDENTE, new ConviteRequestDTO
                ("Teste"), id)).isInstanceOf(ConviteExistente.class);
        verify(conviteRepository, times(1)).existsByMatchIdAndConviteStatusIn(id, statuses);
        verify(conviteRepository, never()).save(any(Convite.class));
        verify(matchmaking, never()).getUserId(any(Long.class), any(TipoUsuario.class));
        verify(publisher, never()).publishEvent(any(ConviteEnviado.class));
    }

    @Test
    void conviteAceitoPositivo() {
        convite.setConviteStatus(ConviteStatus.PENDENTE);

        Convite convite2 = new Convite.Builder().
                id(id).
                texto("Teste").
                conviteStatus(ConviteStatus.ACEITO).
                matchId(id).
                anfitriaoId(id).
                colegaId(id).
                build();

        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));
        when(conviteRepository.save(any(Convite.class))).thenReturn(convite2);

        conviteService.conviteAceito(id);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, times(1)).save(any(Convite.class));
        verify(publisher, times(1)).publishEvent(any(ConviteAceito.class));
    }

    @Test
    void conviteAceitoNegativo1() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.conviteAceito(id)).isInstanceOf(
                ConviteNaoEncontradoUsandoReferencia.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, never()).save(any(Convite.class));
        verify(publisher, never()).publishEvent(any(ConviteAceito.class));
    }

    @Test
    void conviteAceitoNegativo2() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));

        assertThatThrownBy(() -> conviteService.conviteAceito(id)).isInstanceOf(ConviteStatusException.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, never()).save(any(Convite.class));
        verify(publisher, never()).publishEvent(any(ConviteAceito.class));
    }

    @Test
    void conviteRecusadoPositivo() {
        convite.setConviteStatus(ConviteStatus.PENDENTE);

        Convite convite2 = new Convite.Builder().
                id(id).
                texto("Teste").
                conviteStatus(ConviteStatus.RECUSADO).
                matchId(id).
                anfitriaoId(id).
                colegaId(id).
                build();

        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));
        when(conviteRepository.save(any(Convite.class))).thenReturn(convite2);

        conviteService.conviteRecusado(id);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, times(1)).save(any(Convite.class));
    }

    @Test
    void conviteRecusadoNegativo1() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.conviteRecusado(id)).isInstanceOf(
                ConviteNaoEncontradoUsandoReferencia.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, never()).save(any(Convite.class));
    }

    @Test
    void conviteRecusadoNegativo2() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));

        assertThatThrownBy(() -> conviteService.conviteRecusado(id)).isInstanceOf(ConviteStatusException.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, never()).save(any(Convite.class));
    }

    @Test
    void conviteCanceladoPositivo() {
        convite.setConviteStatus(ConviteStatus.PENDENTE);

        Convite convite2 = new Convite.Builder().
                id(id).
                texto("Teste").
                conviteStatus(ConviteStatus.CANCELADO).
                matchId(id).
                anfitriaoId(id).
                colegaId(id).
                build();

        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));
        when(conviteRepository.save(any(Convite.class))).thenReturn(convite2);

        conviteService.conviteCancelado(id);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, times(1)).save(any(Convite.class));
    }

    @Test
    void conviteCanceladoNegativo1() {
        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conviteService.conviteCancelado(id)).isInstanceOf(
                ConviteNaoEncontradoUsandoReferencia.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, never()).save(any(Convite.class));
    }

    @Test
    void conviteCanceladoNegativo2() {
        convite.setConviteStatus(ConviteStatus.ACEITO);

        when(conviteRepository.findTopByMatchIdOrderByIdDesc(id)).thenReturn(Optional.of(convite));

        assertThatThrownBy(() -> conviteService.conviteCancelado(id)).isInstanceOf(ConviteStatusException.class);
        verify(conviteRepository, times(1)).findTopByMatchIdOrderByIdDesc(id);
        verify(conviteRepository, never()).save(any(Convite.class));
    }
}