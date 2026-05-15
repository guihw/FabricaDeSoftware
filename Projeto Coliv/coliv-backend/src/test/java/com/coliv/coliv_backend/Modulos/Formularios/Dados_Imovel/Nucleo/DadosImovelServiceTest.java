package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelNaoEncontradoUsandoReferencia;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DadosImovelServiceTest {

    @Mock
    private DadosImovelRepository dir;

    @InjectMocks
    private DadosImovelService dis;

    @Captor
    ArgumentCaptor<DadosImovel> diCaptor;

    @Test
    @DisplayName("Buscar Dado do Imovel Retorno Positivo")
    public void buscarDadoImovelRetornoPositivo() {
        Long id = 1L;
        DadosImovel buscado = new DadosImovel("Imóvel no centro da cidade", "Rua xyz", 4);
        buscado.setId(id);

        when(dir.findById(id)).thenReturn(Optional.of(buscado));

        DadosImovel dadosImovel = dis.buscarPorId(id);
        verify(dir, times(1)).findById(id);

        assertThat(dadosImovel.getDescricao()).isEqualTo("Imóvel no centro da cidade");
        assertThat(dadosImovel.getLocalizacao()).isEqualTo("Rua xyz");
    }

    @Test
    @DisplayName("Buscar Dado do Imovel Retorno Negativo")
    public void buscarDadoImovelRetornoNegativo() {
        Long id = -1L;

        when(dir.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.buscarPorId(id)).isInstanceOf(DadosImovelIDNaoEncontrado.class);
        verify(dir, times(1)).findById(id);
    }

    @Test
    @DisplayName("Criar Dados do Imovel")
    public void criarDadosImovel() {
        Long id = 1L;
        DadosImovelDTO dto = new DadosImovelDTO("Imóvel no centro da cidade", "Rua xyz", 3);
        DadosImovel salvo = new DadosImovel("Imóvel no centro da cidade", "Rua xyz", 3);
        salvo.setId(id);

        when(dir.save(any())).thenReturn(salvo);

        DadosImovel dadosImovel = dis.criarDadosImovel(id + 1, dto);
        verify(dir, times(1)).save(diCaptor.capture());
        DadosImovel captura = diCaptor.getValue();

        assertThat(captura.getId()).isNull();
        assertThat(dadosImovel).isNotNull();
        assertThat(dadosImovel.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Editar Dados do Imovel Teste com Retorno Positivo")
    public void editarDadosImovelTesteRetornoPositivo() {
        Long id = 1L, aid = 2L;
        DadosImovelDTO dto = new DadosImovelDTO("Imóvel no centro da cidade", "Rua poc", 5);
        DadosImovel dadosImovel = new DadosImovel("Imóvel no centro da cidade", "Rua xyz", 3);
        DadosImovel dadosEditados = new DadosImovel("Imóvel no centro da cidade", "Rua poc", 5);
        dadosImovel.setId(id);
        dadosImovel.setAnfitriaoId(aid);
        dadosEditados.setId(id);
        dadosEditados.setAnfitriaoId(aid);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.of(dadosImovel));
        when(dir.save(any())).thenReturn(dadosEditados);

        DadosImovel edicao = dis.editarDadosImovel(aid, dto);
        verify(dir, times(1)).findByAnfitriaoId(aid);
        verify(dir, times(1)).save(diCaptor.capture());
        DadosImovel captura = diCaptor.getValue();

        assertThat(edicao).isNotNull();
        assertThat(edicao.getAnfitriaoId()).isEqualTo(aid);
        assertThat(edicao.getId()).isEqualTo(dadosImovel.getId());
        assertThat(captura.getQuartos()).isEqualTo(edicao.getQuartos());
    }

    @Test
    @DisplayName("Editar Dados do Imovel Teste com Retorno Negativo")
    public void editarDadosImovelTesteRetornoNegativo() {
        Long id = -1L, aid = -2L;
        DadosImovelDTO dto = new DadosImovelDTO("Imóvel no centro da cidade", "Rua poc", 5);
        DadosImovel dadosImovel = new DadosImovel("Imóvel no centro da cidade", "Rua xyz", 3);
        dadosImovel.setId(id);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.editarDadosImovel(aid, dto)).isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);
        verify(dir, times(1)).findByAnfitriaoId(aid);
        verify(dir, never()).save(any());
    }

    @Test
    @DisplayName("Remover Dados do Imovel Retorno Positivo")
    public void removerDadosImovelRetornoPositivo() {
        Long id = 1L;
        DadosImovel di = new DadosImovel();
        di.setId(id);

        when(dir.findById(id)).thenReturn(Optional.of(di));
        dis.excluir(id);

        verify(dir, times(1)).findById(id);
        verify(dir, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Remover Dados do Imovel Retorno Negativo")
    public void removerDadosImovelRetornoNegativo() {
        Long id = -1L;

        when(dir.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.excluir(id)).isInstanceOf(DadosImovelIDNaoEncontrado.class);
        verify(dir, times(1)).findById(id);
        verify(dir, never()).deleteById(id);
    }

    @Test
    @DisplayName("Get Dados do Imovel Interno Retorno Positivo")
    public void getDadosImovelInternoRetornoPositivo() {
        Long id = 1L, aid = 2L;
        DadosImovel dadosImovel = new DadosImovel("Imóvel no centro da cidade", "Rua poc", 5);
        dadosImovel.setId(id);
        dadosImovel.setAnfitriaoId(aid);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.of(dadosImovel));

        DadosImovelDTO retornado = dis.getDadosImovel(aid);
        verify(dir, times(1)).findByAnfitriaoId(aid);

        assertThat(retornado.descricao()).isEqualTo("Imóvel no centro da cidade");
        assertThat(retornado.localizacao()).isEqualTo("Rua poc");
        assertThat(retornado.quartos()).isEqualTo(5);
    }

    @Test
    @DisplayName("Get Dados do Imovel Interno Retorno Negativo")
    public void getDadosImovelInternoRetornoNegativo() {
        Long aid = -2L;

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.getDadosImovel(aid)).isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);
        verify(dir, times(1)).findByAnfitriaoId(aid);
    }

    @Test
    @DisplayName("Anfitriao Criado Evento")
    public void anfitriaoCriadoEvento() {
        Long aid = 1L;
        DadosImovel dadosSalvo = new DadosImovel();
        UsuarioAnfitriaoCriado evento = new UsuarioAnfitriaoCriado(aid);
        dadosSalvo.setId(aid);
        dadosSalvo.setAnfitriaoId(aid);

        when(dir.save(any(DadosImovel.class))).thenReturn(dadosSalvo);

        dis.eventoAnfitriaoCriado(evento);
        verify(dir, times(1)).save(diCaptor.capture());
        DadosImovel captura = diCaptor.getValue();

        assertThat(captura.getId()).isNull();
        assertThat(captura.getAnfitriaoId()).isEqualTo(dadosSalvo.getAnfitriaoId());
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento Retorno Positivo")
    public void anfitriaoExcluidoEventoRetornoPositivo() {
        Long aid = 1L;
        DadosImovel dadosImovel = new DadosImovel();
        AnfitriaoExcluido evento = new AnfitriaoExcluido(aid);
        dadosImovel.setId(aid);
        dadosImovel.setAnfitriaoId(aid);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.of(dadosImovel));

        dis.eventoAnfitriaoExcluido(evento);
        verify(dir, times(1)).findByAnfitriaoId(aid);
        verify(dir, times(1)).deleteById(aid);
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento Retorno Negativo")
    public void anfitriaoExcluidoEventoRetornoNegativo() {
        Long aid = -1L;
        AnfitriaoExcluido evento = new AnfitriaoExcluido(aid);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.eventoAnfitriaoExcluido(evento)).isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);
        verify(dir, times(1)).findByAnfitriaoId(aid);
        verify(dir, never()).deleteById(aid);
    }
}