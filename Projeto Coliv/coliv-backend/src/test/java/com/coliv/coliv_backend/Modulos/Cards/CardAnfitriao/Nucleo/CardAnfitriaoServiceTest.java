package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardAnfitriaoServiceTest {

    @Mock
    private CardAnfitriaoRepository car;

    @InjectMocks
    private CardAnfitriaoService cas;

    @Captor
    ArgumentCaptor<CardAnfitriao> caCaptor;

    @Test
    @DisplayName("Buscar Card de Anfitriao Retorno Positivo")
    public void buscarCardAnfitriaoRetornoPositivo() {
        Long id = 1L;
        CardAnfitriao card = new CardAnfitriao(5D, 512D);
        card.setId(id);

        when(car.findById(id)).thenReturn(Optional.of(card));

        CardAnfitriao retorno = cas.buscarPorId(id);
        verify(car, times(1)).findById(id);

        assertThat(retorno).isNotNull();
        assertThat(retorno.getClassificacao()).isEqualTo(card.getClassificacao());
    }

    @Test
    @DisplayName("Buscar Card de Anfitriao Retorno Negativo")
    public void buscarCardAnfitriaoRetornoNegativo() {
        Long id = -1L;

        when(car.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.buscarPorId(id)).isInstanceOf(CardAnfitriaoIDNaoEncontrado.class);
        verify(car, times(1)).findById(id);
    }

    @Test
    @DisplayName("Criar Card de Anfitriao")
    public void criarCardAnfitriao() {
        Long id = 1L;
        CardAnfitriaoDTO dto = new CardAnfitriaoDTO(5D, 512D);
        CardAnfitriao salvo = new CardAnfitriao(5D, 512D);
        salvo.setId(id);
        salvo.setAnfitriaoId(id + 1L);

        when(car.save(any())).thenReturn(salvo);

        CardAnfitriao retorno = cas.criarCardAnfitriao(id + 1L, dto);
        verify(car, times(1)).save(caCaptor.capture());
        CardAnfitriao captura = caCaptor.getValue();

        assertThat(retorno).isNotNull();
        assertThat(captura.getId()).isNull();
        assertThat(retorno.getId()).isEqualTo(salvo.getId());
        assertThat(retorno.getAnfitriaoId()).isEqualTo(id + 1L);
    }

    @Test
    @DisplayName("Editar Card de Anfitriao Teste com Retorno Positivo")
    public void editarCardAnfitriaoTesteRetornoPositivo() {
        Long id = 1L, aid = 2L;
        CardAnfitriaoDTO dto = new CardAnfitriaoDTO(5D, 512D);
        CardAnfitriao card = new CardAnfitriao(2.5D, 256D);
        CardAnfitriao salvo = new CardAnfitriao(5D, 512D);
        card.setId(id);
        card.setAnfitriaoId(aid);
        salvo.setId(id);
        salvo.setAnfitriaoId(aid);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.of(card));
        when(car.save(any())).thenReturn(salvo);

        CardAnfitriao retorno = cas.editarCardAnfitriao(aid, dto);
        verify(car, times(1)).findByAnfitriaoId(aid);
        verify(car, times(1)).save(caCaptor.capture());
        CardAnfitriao captura = caCaptor.getValue();

        assertThat(retorno).isNotNull();
        assertThat(retorno.getId()).isEqualTo(card.getId());
        assertThat(retorno.getAnfitriaoId()).isEqualTo(aid);
        assertThat(retorno.getPrecoMensal()).isEqualTo(512D);
    }

    @Test
    @DisplayName("Editar Card de Anfitriao Teste com Retorno Negativo")
    public void editarCardAnfitriaoTesteRetornoNegativo() {
        Long aid = -2L;
        CardAnfitriaoDTO dto = new CardAnfitriaoDTO(5D, 512D);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.editarCardAnfitriao(aid, dto)).isInstanceOf(
                CardAnfitriaoNaoEncontradoUsandoReferencia.class);
        verify(car, times(1)).findByAnfitriaoId(aid);
        verify(car, never()).save(any());
    }

    @Test
    @DisplayName("Remover Card de Anfitriao Teste com Retorno Positivo")
    public void removerCardAnfitriaoTesteRetornoPositivo() {
        Long id = 1L;
        CardAnfitriao card = new CardAnfitriao();
        card.setId(id);

        when(car.findById(id)).thenReturn(Optional.of(card));

        cas.excluir(id);
        verify(car, times(1)).findById(id);
        verify(car, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Remover Card de Anfitriao Teste com Retorno Negativo")
    public void removerCardAnfitriaoTesteRetornoNegativo() {
        Long id = -1L;

        when(car.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.excluir(id)).isInstanceOf(CardAnfitriaoIDNaoEncontrado.class);
        verify(car, times(1)).findById(id);
        verify(car, never()).deleteById(id);
    }

    @Test
    @DisplayName("Anfitriao Criado Evento")
    public void anfitriaoCriadoEvento() {
        Long aid = 2L;
        CardAnfitriao card = new CardAnfitriao();
        UsuarioAnfitriaoCriado evento = new UsuarioAnfitriaoCriado(aid);
        card.setId(aid);
        card.setAnfitriaoId(aid);
        card.setPrecoMensal(128D);

        when(car.save(any(CardAnfitriao.class))).thenReturn(card);

        cas.eventoAnfitriaoCriado(evento);
        verify(car, times(1)).save(caCaptor.capture());
        CardAnfitriao captura = caCaptor.getValue();

        assertThat(captura).isNotNull();
        assertThat(captura.getId()).isNull();
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento Retorno Positivo")
    public void anfitriaoExcluidoEventoRetornoPositivo() {
        Long aid = 1L;
        CardAnfitriao card = new CardAnfitriao();
        AnfitriaoExcluido evento = new AnfitriaoExcluido(aid);
        card.setId(aid);
        card.setAnfitriaoId(aid);
        card.setPrecoMensal(128D);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.of(card));

        cas.eventoAnfitriaoExcluido(evento);
        verify(car, times(1)).findByAnfitriaoId(aid);
        verify(car, times(1)).deleteById(card.getId());
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento Retorno Negativo")
    public void anfitriaoExcluidoEventoRetornoNegativo() {
        Long aid = -1L;
        AnfitriaoExcluido evento = new AnfitriaoExcluido(aid);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.eventoAnfitriaoExcluido(evento)).isInstanceOf(
                CardAnfitriaoNaoEncontradoUsandoReferencia.class);
        verify(car, times(1)).findByAnfitriaoId(aid);
        verify(car, never()).deleteById(aid);
    }
}