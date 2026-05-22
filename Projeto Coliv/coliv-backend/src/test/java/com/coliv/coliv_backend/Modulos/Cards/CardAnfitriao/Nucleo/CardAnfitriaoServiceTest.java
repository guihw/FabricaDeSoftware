package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoRequestDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.IDadosImovel;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardAnfitriaoServiceTest {

    @Mock
    private CardAnfitriaoRepository car;
    @Mock
    private IAnfitriao iAnfitriao;
    @Mock
    private IDadosImovel iDadosImovel;

    @InjectMocks
    private CardAnfitriaoService cas;

    @Captor
    ArgumentCaptor<CardAnfitriao> caCaptor;

    @Test
    @DisplayName("Buscar Card de Anfitriao Retorno Positivo")
    public void buscarCardAnfitriaoRetornoPositivo() {
        Long id = 1L;
        CardAnfitriao card = new CardAnfitriao(new BigDecimal("512.00"));
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
    @DisplayName("Obter Informacao Card Anfitriao Retorno Positivo")
    public void obterInformacaoCardAnfitriaoRetornoPositivo () {
        Long id = 1L;
        UsuarioDTO usuario = new UsuarioDTO(id, "Teste", "teste1email@email.com");
        DadosImovelDTO dados = new DadosImovelDTO(id, "Descricao", "Localizacao", 3);
        CardAnfitriao card = new CardAnfitriao(new BigDecimal("64.00"));

        when(iAnfitriao.obterUsuario(id)).thenReturn(usuario);
        when(iDadosImovel.getDadosImovel(id)).thenReturn(dados);
        when(car.findByAnfitriaoId(id)).thenReturn(Optional.of(card));

        CardAnfitriaoResponseDTO dto = cas.getCardCompleteInfo(id);
        verify(iAnfitriao, times(1)).obterUsuario(id);
        verify(iDadosImovel, times(1)).getDadosImovel(id);
        verify(car, times(1)).findByAnfitriaoId(id);

        assertThat(dto).isNotNull();
        assertThat(dto.descricao()).isEqualTo(dados.descricao());
        assertThat(dto.nome()).isEqualTo(usuario.nome());
        assertThat(dto.precoMensal()).isEqualTo(card.getPrecoMensal());
    }

    @Test
    @DisplayName("Obter Informacao Card Anfitriao Retorno Negativo 1")
    public void obterInformacaoCardAnfitriaoRetornoNegativo1 () {
        Long id = -1L;

        when(iAnfitriao.obterUsuario(id)).thenThrow(new UsuarioIDNaoEncontrado(id));

        assertThatThrownBy(() -> cas.getCardCompleteInfo(id)).isInstanceOf(UsuarioIDNaoEncontrado.class);
        verify(iAnfitriao, times(1)).obterUsuario(id);
        verify(iDadosImovel, never()).getDadosImovel(id);
        verify(car, never()).findByAnfitriaoId(id);
    }

    @Test
    @DisplayName("Obter Informacao Card Anfitriao Retorno Negativo 2")
    public void obterInformacaoCardAnfitriaoRetornoNegativo2 () {
        Long id = -1L;
        UsuarioDTO usuario = new UsuarioDTO(id, "Teste", "teste1email@email.com");

        when(iAnfitriao.obterUsuario(id)).thenReturn(usuario);
        when(iDadosImovel.getDadosImovel(id)).thenThrow(new DadosImovelNaoEncontradoUsandoReferencia(id));

        assertThatThrownBy(() -> cas.getCardCompleteInfo(id)).isInstanceOf(
                DadosImovelNaoEncontradoUsandoReferencia.class);
        verify(iAnfitriao, times(1)).obterUsuario(id);
        verify(iDadosImovel, times(1)).getDadosImovel(id);
        verify(car, never()).findByAnfitriaoId(id);
    }

    @Test
    @DisplayName("Obter Informacao Card Anfitriao Retorno Negativo 3")
    public void obterInformacaoCardAnfitriaoRetornoNegativo3 () {
        Long id = -1L;
        UsuarioDTO usuario = new UsuarioDTO(id, "Teste", "teste1email@email.com");
        DadosImovelDTO dados = new DadosImovelDTO(id, "Descricao", "Localizacao", 3);

        when(iAnfitriao.obterUsuario(id)).thenReturn(usuario);
        when(iDadosImovel.getDadosImovel(id)).thenReturn(dados);
        when(car.findByAnfitriaoId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.getCardCompleteInfo(id)).isInstanceOf(
                CardAnfitriaoNaoEncontradoUsandoReferencia.class);
        verify(iAnfitriao, times(1)).obterUsuario(id);
        verify(iDadosImovel, times(1)).getDadosImovel(id);
        verify(car, times(1)).findByAnfitriaoId(id);
    }

    @Test
    @DisplayName("Criar Card de Anfitriao")
    public void criarCardAnfitriao() {
        Long id = 1L;
        CardAnfitriaoRequestDTO dto = new CardAnfitriaoRequestDTO("512.00");
        CardAnfitriao salvo = new CardAnfitriao(new BigDecimal("512.00"));
        salvo.setId(id);
        salvo.setAnfitriaoId(id + 1L);

        when(car.save(any())).thenReturn(salvo);

        CardAnfitriaoRequestDTO retorno = cas.criarCardAnfitriao(id + 1L, dto);
        verify(car, times(1)).save(caCaptor.capture());
        CardAnfitriao captura = caCaptor.getValue();

        assertThat(retorno).isNotNull();
        assertThat(captura.getId()).isNull();
    }

    @Test
    @DisplayName("Editar Card de Anfitriao Teste com Retorno Positivo")
    public void editarCardAnfitriaoTesteRetornoPositivo() {
        Long id = 1L, aid = 2L;
        CardAnfitriaoRequestDTO dto = new CardAnfitriaoRequestDTO("512.00");
        CardAnfitriao card = new CardAnfitriao(new BigDecimal("256.00"));
        CardAnfitriao salvo = new CardAnfitriao(new BigDecimal("512.00"));
        card.setId(id);
        card.setAnfitriaoId(aid);
        salvo.setId(id);
        salvo.setAnfitriaoId(aid);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.of(card));
        when(car.save(any())).thenReturn(salvo);

        CardAnfitriaoRequestDTO retorno = cas.editarCardAnfitriao(aid, dto);
        verify(car, times(1)).findByAnfitriaoId(aid);
        verify(car, times(1)).save(caCaptor.capture());
        CardAnfitriao captura = caCaptor.getValue();

        assertThat(retorno).isNotNull();
        assertThat(captura).isNotNull();
        assertThat(retorno.precoMensal()).isEqualTo("512.00");
    }

    @Test
    @DisplayName("Editar Card de Anfitriao Teste com Retorno Negativo")
    public void editarCardAnfitriaoTesteRetornoNegativo() {
        Long aid = -2L;
        CardAnfitriaoRequestDTO dto = new CardAnfitriaoRequestDTO("512.00");

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
        card.setPrecoMensal(new BigDecimal("128.00"));

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
        card.setPrecoMensal(new BigDecimal("128.00"));

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