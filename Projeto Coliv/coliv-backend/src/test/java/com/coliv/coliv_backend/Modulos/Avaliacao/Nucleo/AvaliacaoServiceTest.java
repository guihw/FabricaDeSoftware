package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.AvaliacaoDTO;
import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.AvaliacaoIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.AvaliacaoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository repository;

    @InjectMocks
    private AvaliacaoService service;

    private Avaliacao avaliacao;

    @BeforeEach
    void setup() {

        avaliacao = new Avaliacao();

        avaliacao.setId(1L);
        avaliacao.setCardId(10L);
        avaliacao.setUsuarioId(20L);
        avaliacao.setValorClassificacao(4.5f);
    }

    @Test
    void deveCriarAvaliacao() {

        AvaliacaoDTO dto = new AvaliacaoDTO(
                10L,
                20L,
                4.5f
        );

        when(repository.save(any(Avaliacao.class)))
                .thenReturn(avaliacao);

        Avaliacao resultado = service.criar(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getCardId());
        assertEquals(20L, resultado.getUsuarioId());
        assertEquals(4.5f, resultado.getValorClassificacao());

        verify(repository).save(any(Avaliacao.class));
    }

    @Test
    void deveListarAvaliacoes() {

        when(repository.findAll())
                .thenReturn(List.of(avaliacao));

        List<Avaliacao> resultado = service.listar();

        assertEquals(1, resultado.size());

        verify(repository).findAll();
    }

    @Test
    void deveBuscarAvaliacaoPorId() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        Avaliacao resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.getId());

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarAvaliacaoInexistente() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AvaliacaoIdNaoEncontrado.class,
                () -> service.buscarPorId(1L)
        );
    }

    @Test
    void deveEditarAvaliacao() {

        AvaliacaoDTO dto = new AvaliacaoDTO(
                30L,
                40L,
                5.0f
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        when(repository.save(any(Avaliacao.class)))
                .thenReturn(avaliacao);

        Avaliacao resultado = service.editar(1L, dto);

        assertNotNull(resultado);

        verify(repository).findById(1L);
        verify(repository).save(any(Avaliacao.class));
    }

    @Test
    void deveExcluirAvaliacao() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deveRetornarAvaliacaoResponse() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        AvaliacaoResponse response =
                service.getAvaliacao(1L);

        assertNotNull(response);

        assertEquals(1L, response.id());
        assertEquals(10L, response.cardId());
        assertEquals(20L, response.usuarioId());
        assertEquals(4.5f, response.valorClassificacao());
    }
}