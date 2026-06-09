package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoDTO;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoEditada;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoIdNaoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DivisaoServiceTest {

    @Mock
    private DivisaoRepository repository;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private DivisaoService service;

    private Divisao divisao;

    @BeforeEach
    void setup() {

        divisao = new Divisao();

        divisao.setId(1L);
        divisao.setDespesaId(10L);
        divisao.setUsuarioId(20L);
        divisao.setArquivoId(30L);
        divisao.setValor(50.0);
    }

    @Test
    void deveCriarDivisao() {

        DivisaoDTO dto =
                new DivisaoDTO(
                        10L,
                        20L,
                        30L,
                        50.0
                );

        when(repository.save(any(Divisao.class)))
                .thenReturn(divisao);

        Divisao resultado =
                service.criar(dto);

        assertNotNull(resultado);

        verify(repository).save(any(Divisao.class));
    }

    @Test
    void deveListarDivisoes() {

        when(repository.findAll())
                .thenReturn(List.of(divisao));

        List<Divisao> lista =
                service.listar();

        assertEquals(1, lista.size());
    }

    @Test
    void deveBuscarPorId() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(divisao));

        Divisao resultado =
                service.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrar() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                DivisaoIdNaoEncontrado.class,
                () -> service.buscarPorId(1L)
        );
    }

    @Test
    void deveEditarDivisao() {

        DivisaoDTO dto =
                new DivisaoDTO(
                        11L,
                        21L,
                        31L,
                        70.0
                );

        when(repository.findById(1L))
                .thenReturn(Optional.of(divisao));

        when(repository.save(any(Divisao.class)))
                .thenReturn(divisao);

        Divisao resultado =
                service.editar(1L, dto);

        assertNotNull(resultado);

        verify(repository).save(any(Divisao.class));
        verify(publisher).publishEvent(any(DivisaoEditada.class));
    }

    @Test
    void deveExcluirDivisao() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(divisao));

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}