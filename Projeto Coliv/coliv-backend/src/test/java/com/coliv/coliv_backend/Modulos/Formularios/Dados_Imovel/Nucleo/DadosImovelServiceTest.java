package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelRequestDTO;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DadosImovelServiceTest {

    @Mock  private DadosImovelRepository dir;
    @InjectMocks private DadosImovelService dis;
    @Captor ArgumentCaptor<DadosImovel> diCaptor;

    private DadosImovelRequestDTO requestDTOCompleto() {
        return new DadosImovelRequestDTO(
                "Apartamento no centro",
                "Pinheiros, SP",
                3,
                new BigDecimal("2500.00"),
                "Quarto Privativo",
                List.of("wifi", "pet", "academia")
        );
    }

    private DadosImovel entidadeCompleta(Long anfitriaoId) {
        DadosImovel d = new DadosImovel("Apartamento no centro", "Pinheiros, SP", 3);
        d.setAnfitriaoId(anfitriaoId);
        d.setPrecoMensal(new BigDecimal("2500.00"));
        d.setTipoVaga("Quarto Privativo");
        d.setComodidades(List.of("wifi", "pet", "academia"));
        return d;
    }

    @Test
    @DisplayName("Buscar por id — retorno positivo")
    void buscarPorIdPositivo() {
        DadosImovel entidade = entidadeCompleta(1L);
        entidade.setId(1L);

        when(dir.findById(1L)).thenReturn(Optional.of(entidade));

        DadosImovel resultado = dis.buscarPorId(1L);

        assertThat(resultado.getDescricao()).isEqualTo("Apartamento no centro");
        assertThat(resultado.getPrecoMensal()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(resultado.getTipoVaga()).isEqualTo("Quarto Privativo");
        assertThat(resultado.getComodidades()).containsExactlyInAnyOrder("wifi", "pet", "academia");
    }

    @Test
    @DisplayName("Buscar por id — retorno negativo")
    void buscarPorIdNegativo() {
        when(dir.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.buscarPorId(-1L))
                .isInstanceOf(DadosImovelIDNaoEncontrado.class);
    }

    @Test
    @DisplayName("Criar dados do imóvel com todos os novos campos")
    void criarDadosImovelComNovoCampos() {
        Long anfitriaoId = 5L;
        DadosImovelRequestDTO dto = requestDTOCompleto();
        DadosImovel salvo = entidadeCompleta(anfitriaoId);
        salvo.setId(10L);

        when(dir.findByAnfitriaoId(anfitriaoId)).thenReturn(Optional.empty());
        when(dir.save(any())).thenReturn(salvo);

        DadosImovelRequestDTO retorno = dis.criarDadosImovel(anfitriaoId, dto);

        verify(dir).save(diCaptor.capture());
        DadosImovel capturado = diCaptor.getValue();

        // id deve ser null antes de salvar (upsert criando novo)
        assertThat(capturado.getId()).isNull();
        assertThat(capturado.getAnfitriaoId()).isEqualTo(anfitriaoId);
        assertThat(capturado.getPrecoMensal()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(capturado.getTipoVaga()).isEqualTo("Quarto Privativo");
        assertThat(capturado.getComodidades()).containsExactlyInAnyOrder("wifi", "pet", "academia");
        assertThat(retorno).isNotNull();
    }

    @Test
    @DisplayName("Criar dados do imóvel — upsert reutiliza registro existente")
    void criarDadosImovelUpsert() {
        Long anfitriaoId = 5L;
        DadosImovel existente = entidadeCompleta(anfitriaoId);
        existente.setId(10L);

        when(dir.findByAnfitriaoId(anfitriaoId)).thenReturn(Optional.of(existente));
        when(dir.save(any())).thenReturn(existente);

        DadosImovelRequestDTO dto = new DadosImovelRequestDTO(
                "Nova descrição", "Nova localização", 2,
                new BigDecimal("1800.00"), "Quarto Compartilhado", List.of("wifi")
        );

        dis.criarDadosImovel(anfitriaoId, dto);

        verify(dir).save(diCaptor.capture());
        DadosImovel capturado = diCaptor.getValue();

        assertThat(capturado.getId()).isEqualTo(10L);
        assertThat(capturado.getPrecoMensal()).isEqualByComparingTo(new BigDecimal("1800.00"));
        assertThat(capturado.getTipoVaga()).isEqualTo("Quarto Compartilhado");
    }

    @Test
    @DisplayName("Criar com comodidades null não lança exceção — salva lista vazia")
    void criarComComodidadesNull() {
        Long anfitriaoId = 5L;
        DadosImovelRequestDTO dtoSemComodidades = new DadosImovelRequestDTO(
                "Desc", "Local", 1,
                new BigDecimal("1000.00"), "Quarto Privativo",
                null   // comodidades null
        );
        DadosImovel salvo = new DadosImovel("Desc", "Local", 1);
        salvo.setAnfitriaoId(anfitriaoId);

        when(dir.findByAnfitriaoId(anfitriaoId)).thenReturn(Optional.empty());
        when(dir.save(any())).thenReturn(salvo);

        dis.criarDadosImovel(anfitriaoId, dtoSemComodidades);

        verify(dir).save(diCaptor.capture());
        assertThat(diCaptor.getValue().getComodidades()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Editar dados do imóvel — atualiza todos os campos novos")
    void editarDadosImovelPositivo() {
        Long anfitriaoId = 5L;
        DadosImovel existente = entidadeCompleta(anfitriaoId);
        existente.setId(10L);

        DadosImovelRequestDTO dto = new DadosImovelRequestDTO(
                "Desc editada", "Novo bairro, SP", 4,
                new BigDecimal("3000.00"), "Suíte Master",
                List.of("piscina", "rooftop")
        );

        when(dir.findByAnfitriaoId(anfitriaoId)).thenReturn(Optional.of(existente));
        when(dir.save(any())).thenReturn(existente);

        DadosImovelRequestDTO retorno = dis.editarDadosImovel(anfitriaoId, dto);

        verify(dir).save(diCaptor.capture());
        DadosImovel capturado = diCaptor.getValue();

        assertThat(capturado.getPrecoMensal()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(capturado.getTipoVaga()).isEqualTo("Suíte Master");
        assertThat(capturado.getComodidades()).containsExactlyInAnyOrder("piscina", "rooftop");
        assertThat(retorno).isNotNull();
    }

    @Test
    @DisplayName("Editar dados do imóvel — retorno negativo")
    void editarDadosImovelNegativo() {
        when(dir.findByAnfitriaoId(-2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.editarDadosImovel(-2L, requestDTOCompleto()))
                .isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);

        verify(dir, never()).save(any());
    }

    @Test
    @DisplayName("Excluir — retorno positivo")
    void excluirPositivo() {
        DadosImovel d = new DadosImovel();
        d.setId(1L);

        when(dir.findById(1L)).thenReturn(Optional.of(d));

        dis.excluir(1L);

        verify(dir).deleteById(1L);
    }

    @Test
    @DisplayName("Excluir — retorno negativo")
    void excluirNegativo() {
        when(dir.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.excluir(-1L))
                .isInstanceOf(DadosImovelIDNaoEncontrado.class);

        verify(dir, never()).deleteById(any());
    }

    @Test
    @DisplayName("getDadosImovel — retorna DTO com todos os campos novos")
    void getDadosImovelPositivo() {
        Long anfitriaoId = 2L;
        DadosImovel entidade = entidadeCompleta(anfitriaoId);
        entidade.setId(1L);

        when(dir.findByAnfitriaoId(anfitriaoId)).thenReturn(Optional.of(entidade));

        DadosImovelDTO retorno = dis.getDadosImovel(anfitriaoId);

        assertThat(retorno.descricao()).isEqualTo("Apartamento no centro");
        assertThat(retorno.localizacao()).isEqualTo("Pinheiros, SP");
        assertThat(retorno.precoMensal()).isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(retorno.tipoVaga()).isEqualTo("Quarto Privativo");
        assertThat(retorno.comodidades()).containsExactlyInAnyOrder("wifi", "pet", "academia");
    }

    @Test
    @DisplayName("getDadosImovel — retorno negativo")
    void getDadosImovelNegativo() {
        when(dir.findByAnfitriaoId(-2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.getDadosImovel(-2L))
                .isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);
    }

    @Test
    @DisplayName("Anfitriao Criado Evento — cria registro vazio")
    void anfitriaoCriadoEvento() {
        Long aid = 1L;
        DadosImovel salvo = new DadosImovel();
        salvo.setId(aid);
        salvo.setAnfitriaoId(aid);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.empty());
        when(dir.save(any(DadosImovel.class))).thenReturn(salvo);

        dis.eventoAnfitriaoCriado(new UsuarioAnfitriaoCriado(aid));

        verify(dir).save(diCaptor.capture());
        assertThat(diCaptor.getValue().getId()).isNull();
        assertThat(diCaptor.getValue().getAnfitriaoId()).isEqualTo(aid);
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento — retorno positivo")
    void anfitriaoExcluidoEventoPositivo() {
        Long aid = 1L;
        DadosImovel dadosImovel = new DadosImovel();
        dadosImovel.setId(aid);
        dadosImovel.setAnfitriaoId(aid);

        when(dir.findByAnfitriaoId(aid)).thenReturn(Optional.of(dadosImovel));

        dis.eventoAnfitriaoExcluido(new AnfitriaoExcluido(aid));

        verify(dir).deleteById(aid);
    }

    @Test
    @DisplayName("Anfitriao Excluido Evento — retorno negativo")
    void anfitriaoExcluidoEventoNegativo() {
        when(dir.findByAnfitriaoId(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dis.eventoAnfitriaoExcluido(new AnfitriaoExcluido(-1L)))
                .isInstanceOf(DadosImovelNaoEncontradoUsandoReferencia.class);

        verify(dir, never()).deleteById(any());
    }
}