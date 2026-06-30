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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardAnfitriaoServiceTest {

    @Mock private CardAnfitriaoRepository car;
    @Mock private IAnfitriao iAnfitriao;
    @Mock private IDadosImovel iDadosImovel;

    @InjectMocks private CardAnfitriaoService cas;

    @Captor ArgumentCaptor<CardAnfitriao> caCaptor;

    /** DadosImovelDTO com todos os campos novos preenchidos */
    private DadosImovelDTO dadosMock(Long anfitriaoId) {
        return new DadosImovelDTO(
                anfitriaoId,
                "Apartamento espaçoso",
                "Pinheiros, SP",
                2,
                new BigDecimal("2500.00"),
                "Quarto Privativo",
                List.of("wifi", "pet", "academia")
        );
    }

    private UsuarioDTO usuarioMock(Long id) {
        return new UsuarioDTO(id, "Teste", "teste@email.com", false, null);
    }

    @Test
    @DisplayName("Buscar Card de Anfitriao Retorno Positivo")
    void buscarCardAnfitriaoRetornoPositivo() {
        Long id = 1L;
        CardAnfitriao card = new CardAnfitriao(new BigDecimal("512.00"));
        card.setId(id);

        when(car.findById(id)).thenReturn(Optional.of(card));

        CardAnfitriao retorno = cas.buscarPorId(id);
        verify(car).findById(id);

        assertThat(retorno).isNotNull();
    }

    @Test
    @DisplayName("Buscar Card de Anfitriao Retorno Negativo")
    void buscarCardAnfitriaoRetornoNegativo() {
        when(car.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.buscarPorId(-1L))
                .isInstanceOf(CardAnfitriaoIDNaoEncontrado.class);
    }


    @Test
    @DisplayName("Obter Info Card — precoMensal, tipoVaga e comodidades vêm de DadosImovel")
    void obterInfoCardComNovosCampos() {
        Long id = 1L;
        UsuarioDTO usuario = usuarioMock(id);
        DadosImovelDTO dados = dadosMock(id);
        CardAnfitriao card = new CardAnfitriao(BigDecimal.ZERO); // preço no card é irrelevante agora
        card.setAnfitriaoId(id);

        when(iAnfitriao.obterUsuario(id)).thenReturn(usuario);
        when(iDadosImovel.getDadosImovel(id)).thenReturn(dados);
        when(car.findByAnfitriaoId(id)).thenReturn(Optional.of(card));

        CardAnfitriaoResponseDTO dto = cas.getCardCompleteInfo(id);

        assertThat(dto).isNotNull();
        assertThat(dto.precoMensal()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(dto.tipoVaga()).isEqualTo("Quarto Privativo");
        assertThat(dto.comodidades()).containsExactlyInAnyOrder("wifi", "pet", "academia");
        assertThat(dto.descricao()).isEqualTo("Apartamento espaçoso");
        assertThat(dto.localizacao()).isEqualTo("Pinheiros, SP");
        assertThat(dto.nome()).isEqualTo(usuario.nome());
    }

    @Test
    @DisplayName("Obter Info Card — precoMensal zero quando DadosImovel não tem preço")
    void obterInfoCardSemPreco() {
        Long id = 2L;
        DadosImovelDTO dadosSemPreco = new DadosImovelDTO(
                id, "Desc", "Local", 1,
                null,           // sem preço
                "Quarto Compartilhado",
                List.of()
        );
        CardAnfitriao card = new CardAnfitriao(BigDecimal.ZERO);
        card.setAnfitriaoId(id);

        when(iAnfitriao.obterUsuario(id)).thenReturn(usuarioMock(id));
        when(iDadosImovel.getDadosImovel(id)).thenReturn(dadosSemPreco);
        when(car.findByAnfitriaoId(id)).thenReturn(Optional.of(card));

        CardAnfitriaoResponseDTO dto = cas.getCardCompleteInfo(id);

        // deve retornar 0 para o frontend exibir "A consultar"
        assertThat(dto.precoMensal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Obter Info Card — comodidades lista vazia quando não cadastradas")
    void obterInfoCardSemComodidades() {
        Long id = 3L;
        DadosImovelDTO dadosSemComodidades = new DadosImovelDTO(
                id, "Desc", "Local", 1,
                new BigDecimal("1500.00"), "Quarto Privativo",
                null   // comodidades null
        );
        CardAnfitriao card = new CardAnfitriao(BigDecimal.ZERO);
        card.setAnfitriaoId(id);

        when(iAnfitriao.obterUsuario(id)).thenReturn(usuarioMock(id));
        when(iDadosImovel.getDadosImovel(id)).thenReturn(dadosSemComodidades);
        when(car.findByAnfitriaoId(id)).thenReturn(Optional.of(card));

        CardAnfitriaoResponseDTO dto = cas.getCardCompleteInfo(id);

        assertThat(dto.comodidades()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Obter Info Card — falha quando usuário não encontrado")
    void obterInfoCardUsuarioNaoEncontrado() {
        when(iAnfitriao.obterUsuario(-1L)).thenThrow(new UsuarioIDNaoEncontrado(-1L));

        assertThatThrownBy(() -> cas.getCardCompleteInfo(-1L))
                .isInstanceOf(UsuarioIDNaoEncontrado.class);

        verify(iDadosImovel, never()).getDadosImovel(any());
        verify(car, never()).findByAnfitriaoId(any());
    }

    @Test
    @DisplayName("Obter Info Card — falha quando dados do imóvel não encontrados")
    void obterInfoCardDadosImovelNaoEncontrado() {
        Long id = -1L;
        when(iAnfitriao.obterUsuario(id)).thenReturn(usuarioMock(id));
        when(iDadosImovel.getDadosImovel(id))
                .thenThrow(new DadosImovelNaoEncontradoUsandoReferencia(id));

        assertThatThrownBy(() -> cas.getCardCompleteInfo(id))
                .isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);

        verify(car, never()).findByAnfitriaoId(any());
    }

    @Test
    @DisplayName("Obter Info Card — falha quando card não encontrado")
    void obterInfoCardCardNaoEncontrado() {
        Long id = -1L;
        when(iAnfitriao.obterUsuario(id)).thenReturn(usuarioMock(id));
        when(iDadosImovel.getDadosImovel(id)).thenReturn(dadosMock(id));
        when(car.findByAnfitriaoId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.getCardCompleteInfo(id))
                .isInstanceOf(CardAnfitriaoNaoEncontradoUsandoReferencia.class);
    }

    @Test
    @DisplayName("Criar Card de Anfitriao")
    void criarCardAnfitriao() {
        Long id = 1L;
        CardAnfitriaoRequestDTO dto = new CardAnfitriaoRequestDTO("0");
        CardAnfitriao salvo = new CardAnfitriao(BigDecimal.ZERO);
        salvo.setId(id);
        salvo.setAnfitriaoId(id + 1L);

        when(car.save(any())).thenReturn(salvo);

        cas.criarCardAnfitriao(id + 1L, dto);
        verify(car).save(caCaptor.capture());

        assertThat(caCaptor.getValue().getId()).isNull();
    }

    @Test
    @DisplayName("Editar Card de Anfitriao — Retorno Positivo")
    void editarCardAnfitriaoPositivo() {
        Long aid = 2L;
        CardAnfitriaoRequestDTO dto = new CardAnfitriaoRequestDTO("0");
        CardAnfitriao card = new CardAnfitriao(BigDecimal.ZERO);
        card.setId(1L);
        card.setAnfitriaoId(aid);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.of(card));
        when(car.save(any())).thenReturn(card);

        CardAnfitriaoRequestDTO retorno = cas.editarCardAnfitriao(aid, dto);

        assertThat(retorno).isNotNull();
        verify(car).save(any());
    }

    @Test
    @DisplayName("Editar Card de Anfitriao — Retorno Negativo")
    void editarCardAnfitriaoNegativo() {
        when(car.findByAnfitriaoId(-2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.editarCardAnfitriao(-2L, new CardAnfitriaoRequestDTO("0")))
                .isInstanceOf(CardAnfitriaoNaoEncontradoUsandoReferencia.class);

        verify(car, never()).save(any());
    }

    @Test
    @DisplayName("Remover Card — Retorno Positivo")
    void removerCardPositivo() {
        CardAnfitriao card = new CardAnfitriao();
        card.setId(1L);

        when(car.findById(1L)).thenReturn(Optional.of(card));

        cas.excluir(1L);

        verify(car).deleteById(1L);
    }

    @Test
    @DisplayName("Remover Card — Retorno Negativo")
    void removerCardNegativo() {
        when(car.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.excluir(-1L))
                .isInstanceOf(CardAnfitriaoIDNaoEncontrado.class);

        verify(car, never()).deleteById(any());
    }

    @Test
    @DisplayName("Anfitriao Criado Evento — cria card com preço zero")
    void anfitriaoCriadoEvento() {
        Long aid = 2L;
        CardAnfitriao salvo = new CardAnfitriao();
        salvo.setId(aid);
        salvo.setAnfitriaoId(aid);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.empty());
        when(car.save(any(CardAnfitriao.class))).thenReturn(salvo);

        cas.eventoAnfitriaoCriado(new UsuarioAnfitriaoCriado(aid));

        verify(car).save(caCaptor.capture());
        assertThat(caCaptor.getValue().getId()).isNull();
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento — Retorno Positivo")
    void anfitriaoExcluidoEventoPositivo() {
        Long aid = 1L;
        CardAnfitriao card = new CardAnfitriao();
        card.setId(aid);
        card.setAnfitriaoId(aid);

        when(car.findByAnfitriaoId(aid)).thenReturn(Optional.of(card));

        cas.eventoAnfitriaoExcluido(new AnfitriaoExcluido(aid));

        verify(car).deleteById(card.getId());
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento — Retorno Negativo")
    void anfitriaoExcluidoEventoNegativo() {
        when(car.findByAnfitriaoId(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cas.eventoAnfitriaoExcluido(new AnfitriaoExcluido(-1L)))
                .isInstanceOf(CardAnfitriaoNaoEncontradoUsandoReferencia.class);

        verify(car, never()).deleteById(any());
    }
}