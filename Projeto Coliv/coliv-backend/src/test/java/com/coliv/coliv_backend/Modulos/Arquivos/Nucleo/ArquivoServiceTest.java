package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoDTO;
import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArquivoServiceTest {

    @Mock
    private ArquivoRepository repository;

    @InjectMocks
    private ArquivoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveBuscarArquivoPorId() {

        Arquivo arquivo =
                new Arquivo(
                        "https://teste.com/foto.jpg",
                        TipoArquivo.IMAGEM
                );

        when(repository.findById(1L))
                .thenReturn(Optional.of(arquivo));

        ArquivoDTO resultado =
                service.getArquivo(1L);

        assertEquals(
                "https://teste.com/foto.jpg",
                resultado.url()
        );

        assertEquals(
                TipoArquivo.IMAGEM,
                resultado.type()
        );
    }

    @Test
    void deveLancarErroQuandoArquivoNaoExistir() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ArquivoNaoEncontrado.class,
                () -> service.getArquivo(99L)
        );
    }

    @Test
    void deveCriarArquivo() {

        ArquivoRequestDTO dto =
                new ArquivoRequestDTO(
                        "https://teste.com/foto.jpg",
                        TipoArquivo.IMAGEM
                );

        Arquivo arquivo =
                new Arquivo(
                        dto.url(),
                        dto.type()
                );

        when(repository.save(any(Arquivo.class)))
                .thenReturn(arquivo);

        service.criar(dto);

        verify(repository, times(1))
                .save(any(Arquivo.class));
    }

    @Test
    void deveExcluirArquivo() {

        Arquivo arquivo =
                new Arquivo(
                        "https://teste.com/foto.jpg",
                        TipoArquivo.IMAGEM
                );

        when(repository.findById(1L))
                .thenReturn(Optional.of(arquivo));

        service.excluir(1L);

        verify(repository)
                .deleteById(1L);
    }

    @Test
    void deveRetornarListaArquivos() {

        Arquivo arquivo =
                new Arquivo(
                        "https://teste.com/foto.jpg",
                        TipoArquivo.IMAGEM
                );

        when(repository.findAllById(List.of(1L)))
                .thenReturn(List.of(arquivo));

        List<ArquivoDTO> resultado =
                service.getArquivos(List.of(1L));

        assertEquals(1, resultado.size());
    }
}