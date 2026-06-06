package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.ComentarioDTO;
import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.ComentarioIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.ComentarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository repository;

    @InjectMocks
    private ComentarioService service;

    private Comentario comentario;

    @BeforeEach
    void setup() {

        comentario = new Comentario();

        comentario.setId(1L);
        comentario.setCardId(10L);
        comentario.setUsuarioId(20L);
        comentario.setParentId(null);
        comentario.setTexto("Comentário teste");
        comentario.setData(LocalDateTime.now());
    }

    @Test
    void deveCriarComentario() {

        ComentarioDTO dto = new ComentarioDTO(
                10L,
                20L,
                null,
                "Comentário teste"
        );

        when(repository.save(any(Comentario.class)))
                .thenReturn(comentario);

        Comentario resultado = service.criar(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getCardId());
        assertEquals(20L, resultado.getUsuarioId());

        verify(repository).save(any(Comentario.class));
    }

    @Test
    void deveListarComentarios() {

        when(repository.findAll())
                .thenReturn(List.of(comentario));

        List<Comentario> resultado = service.listar();

        assertEquals(1, resultado.size());

        verify(repository).findAll();
    }

    @Test
    void deveBuscarComentarioPorId() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(comentario));

        Comentario resultado = service.buscarPorId(1L);

        assertEquals(1L, resultado.getId());

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarComentarioInexistente() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ComentarioIdNaoEncontrado.class,
                () -> service.buscarPorId(1L)
        );
    }

    @Test
    void deveEditarComentario() {

        ComentarioDTO dto = new ComentarioDTO(
                30L,
                40L,
                null,
                "Texto alterado"
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(comentario));

        when(repository.save(any(Comentario.class)))
                .thenReturn(comentario);

        Comentario resultado = service.editar(1L, dto);

        assertNotNull(resultado);

        verify(repository).findById(1L);
        verify(repository).save(any(Comentario.class));
    }

    @Test
    void deveExcluirComentario() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(comentario));

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deveRetornarComentarioResponse() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(comentario));

        ComentarioResponse response =
                service.getComentario(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(10L, response.cardId());
        assertEquals(20L, response.usuarioId());
    }
}