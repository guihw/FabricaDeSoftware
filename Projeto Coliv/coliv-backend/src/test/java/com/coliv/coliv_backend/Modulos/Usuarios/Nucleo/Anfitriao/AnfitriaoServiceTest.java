package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnfitriaoServiceTest {

    @Mock
    private AnfitriaoRepository anfitriaoRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private AnfitriaoService anfitriaoService;

    @Captor
    ArgumentCaptor<Anfitriao> anfitriaoCaptor;

    @Test
    @DisplayName("Buscar Anfitriao Teste")
    public void buscarAnfitriaoTest() {
        Anfitriao anfitriaoTeste = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                                            "senhateste", false, 0L, 0L, 0L);

        when(anfitriaoRepository.findById(1L)).thenReturn(Optional.of(anfitriaoTeste));

        Anfitriao resultado = anfitriaoService.buscarPorId(1L);

        assertThat(resultado.getNome()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Criar Anfitriao Teste")
    public void criarAnfitriaoTest() {
        Anfitriao anfitriaoSalvo = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false, 0L, 0L, 0L);
        AnfitriaoDTO dto = new AnfitriaoDTO("Teste", "511.995.364-25", "testeemail.com",
                "senhateste", 0L);

        when(anfitriaoRepository.save(any())).thenReturn(anfitriaoSalvo);

        anfitriaoService.criarAnfitriao(dto);
        verify(anfitriaoRepository, times(1)).save(anfitriaoCaptor.capture());
        verify(publisher, times(1)).publishEvent(any(UsuarioAnfitriaoCriado.class));
        Anfitriao anfitriaoCapturado = anfitriaoCaptor.getValue();

        assertThat(anfitriaoCapturado).isNotNull();
        assertThat(anfitriaoCapturado.getId()).isEqualTo(1L);
        assertThat(anfitriaoCapturado.getNome()).isEqualTo("Teste");

    }

}