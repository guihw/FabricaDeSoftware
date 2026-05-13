package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("Buscar Anfitriao Teste Retorno Positivo")
    public void buscarAnfitriaoTesteRetornoPositivo() {
        Anfitriao anfitriaoTeste = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                                            "senhateste", false, 0L, 0L, 0L);

        when(anfitriaoRepository.findById(1L)).thenReturn(Optional.of(anfitriaoTeste));

        Anfitriao resultado = anfitriaoService.buscarPorId(1L);

        assertThat(resultado.getNome()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Buscar Anfitriao Teste Retorno Negativo")
    public void buscarAnfitriaoTesteRetornoNegativo() {
        Anfitriao anfitriaoTeste = new Anfitriao(-1L, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false, 0L, 0L, 0L);

        when(anfitriaoRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anfitriaoService.buscarPorId(-1L)).isInstanceOf(UsuarioIDNaoEncontrado.class);

        verify(anfitriaoRepository, times(1)).findById(-1L);
    }

    @Test
    @DisplayName("Criar Anfitriao Teste")
    public void criarAnfitriaoTeste() {
        Anfitriao anfitriaoSalvo = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false, 0L, 0L, 0L);
        AnfitriaoDTO dto = new AnfitriaoDTO("Teste", "511.995.364-25", "testeemail.com",
                "senhateste", 0L);

        when(anfitriaoRepository.save(any())).thenReturn(anfitriaoSalvo);

        Anfitriao anfitriao = anfitriaoService.criarAnfitriao(dto);
        verify(anfitriaoRepository, times(1)).save(anfitriaoCaptor.capture());
        verify(publisher, times(1)).publishEvent(any(UsuarioAnfitriaoCriado.class));
        Anfitriao anfitriaoCapturado = anfitriaoCaptor.getValue();

        assertThat(anfitriaoCapturado).isNotNull();
        assertThat(anfitriaoCapturado.getId()).isNull();
        assertThat(anfitriaoCapturado.getNome()).isEqualTo("Teste");
        assertThat(anfitriao.getId()).isEqualTo(1L);
        assertThat(anfitriao.getNome()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Editar Anfitriao Teste com Retorno Positivo")
    public void editarAnfitriaoTesteRetornoPositivo () {
        Long id = 1L;
        Anfitriao anfitriao = new Anfitriao(id, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false, 0L, 0L, 0L);
        Anfitriao anfitriaoUpdate = new Anfitriao("Teste1", "511.995.364-25", "teste1email@email.com",
                "senhateste1", 0L);

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.of(anfitriao));
        when(anfitriaoRepository.save(any())).thenReturn(anfitriao);

        Anfitriao anfitriaoEdit = anfitriaoService.editarAnfitriao(id, anfitriaoUpdate);
        verify(anfitriaoRepository, times(1)).findById(id);
        verify(anfitriaoRepository, times(1)).save(anfitriaoCaptor.capture());

        Anfitriao capturado = anfitriaoCaptor.getValue();

        assertThat(anfitriaoEdit).isNotNull();
        assertThat(anfitriaoEdit.getId()).isEqualTo(id);
        assertThat(anfitriaoEdit.getNome()).isEqualTo("Teste1");
        assertThat(anfitriaoEdit.getEmail()).isEqualTo("teste1email@email.com");
        assertThat(anfitriaoEdit.getSenha()).isEqualTo("senhateste1");
    }

    @Test
    @DisplayName("Editar Anfitriao Teste com Retorno Negativo")
    public void editarAnfitriaoTesteRetornoNegativo () {
        Long id = -1L;

        Anfitriao anfitriaoUpdate = new Anfitriao("Teste1", "511.995.364-25", "teste1email@email.com",
                "senhateste1", 0L);

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anfitriaoService.editarAnfitriao(id, anfitriaoUpdate)).isInstanceOf(UsuarioIDNaoEncontrado.class);
        verify(anfitriaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Remover Anfitriao Teste com Retorno Positivo")
    public void removerAnfitriaoTesteRetornoPositivo () {
        Long id = 1L;
        Anfitriao anfitriao = new Anfitriao();
        anfitriao.setId(id);

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.of(anfitriao));

        anfitriaoService.excluir(id);

        verify(anfitriaoRepository, times(1)).findById(id);
        verify(anfitriaoRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Remover Anfitriao Teste com Retorno Negativo")
    public void removerAnfitriaoTesteRetornoNegativo () {
        Long id = -1L;
        Anfitriao anfitriao = new Anfitriao();
        anfitriao.setId(id);

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> anfitriaoService.excluir(id)).isInstanceOf(UsuarioIDNaoEncontrado.class);

        verify(anfitriaoRepository, times(1)).findById(id);
        verify(anfitriaoRepository, never()).deleteById(id);
    }
}