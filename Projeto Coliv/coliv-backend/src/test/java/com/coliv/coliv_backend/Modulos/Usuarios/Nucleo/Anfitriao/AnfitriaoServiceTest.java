package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoPostDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoPutDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.CPFDeUsuarioExistente;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.EmailDeUsuarioExistente;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnfitriaoServiceTest {

    @Mock
    private AnfitriaoRepository anfitriaoRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AnfitriaoService anfitriaoService;

    @Captor
    ArgumentCaptor<Anfitriao> anfitriaoCaptor;

    @Test
    @DisplayName("Buscar Anfitriao Teste Retorno Positivo")
    public void buscarAnfitriaoTesteRetornoPositivo() {
        Anfitriao anfitriaoTeste = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                                            "senhateste", false);

        when(anfitriaoRepository.findById(1L)).thenReturn(Optional.of(anfitriaoTeste));

        UsuarioDTO resultado = anfitriaoService.buscarPorId(1L);

        assertThat(resultado.nome()).isEqualTo("Teste");
        assertThat(resultado.id()).isEqualTo(anfitriaoTeste.getId());
    }

    @Test
    @DisplayName("Buscar Anfitriao Teste Retorno Negativo")
    public void buscarAnfitriaoTesteRetornoNegativo() {
        when(anfitriaoRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anfitriaoService.buscarPorId(-1L)).isInstanceOf(UsuarioIDNaoEncontrado.class);

        verify(anfitriaoRepository, times(1)).findById(-1L);
    }

    @Test
    @DisplayName("Criar Anfitriao Teste Retorno Positivo")
    public void criarAnfitriaoTesteRetornoPositivo() {
        Anfitriao anfitriaoSalvo = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false);
        AnfitriaoPostDTO dto = new AnfitriaoPostDTO("Teste", "511.995.364-25", "testeemail.com",
                "senhateste");

        when(anfitriaoRepository.save(any())).thenReturn(anfitriaoSalvo);
        when(anfitriaoRepository.existsByEmail(dto.email())).thenReturn(false);
        when(anfitriaoRepository.existsByCpf(dto.cpf())).thenReturn(false);

        AnfitriaoPostDTO anfitriaoPostDTO = anfitriaoService.criarAnfitriao(dto);
        verify(anfitriaoRepository, times(1)).existsByEmail(dto.email());
        verify(anfitriaoRepository, times(1)).existsByCpf(dto.cpf());
        verify(anfitriaoRepository, times(1)).save(anfitriaoCaptor.capture());
        verify(passwordEncoder, times(1)).encode(dto.senha());
        verify(publisher, times(1)).publishEvent(any(UsuarioAnfitriaoCriado.class));
        Anfitriao anfitriaoCapturado = anfitriaoCaptor.getValue();

        assertThat(anfitriaoCapturado).isNotNull();
        assertThat(anfitriaoCapturado.getId()).isNull();
        assertThat(anfitriaoCapturado.getNome()).isEqualTo("Teste");
        assertThat(anfitriaoPostDTO.nome()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Criar Anfitriao Teste Retorno Negativo 1")
    public void criarAnfitriaoTesteRetornoNegativo1() {
        AnfitriaoPostDTO dto = new AnfitriaoPostDTO("Teste", "511.995.364-25", "testeemail.com",
                "senhateste");

        when(anfitriaoRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThatThrownBy(() -> anfitriaoService.criarAnfitriao(dto)).isInstanceOf(EmailDeUsuarioExistente.class);
        verify(anfitriaoRepository, never()).save(any());
        verify(anfitriaoRepository, times(1)).existsByEmail(dto.email());
        verify(passwordEncoder, never()).encode(any());
        verify(publisher, never()).publishEvent(any(UsuarioAnfitriaoCriado.class));
    }

    @Test
    @DisplayName("Criar Anfitriao Teste Retorno Negativo 2")
    public void criarAnfitriaoTesteRetornoNegativo2() {
        AnfitriaoPostDTO dto = new AnfitriaoPostDTO("Teste", "511.995.364-25", "testeemail.com",
                "senhateste");

        when(anfitriaoRepository.existsByCpf(dto.cpf())).thenReturn(true);

        assertThatThrownBy(() -> anfitriaoService.criarAnfitriao(dto)).isInstanceOf(CPFDeUsuarioExistente.class);
        verify(anfitriaoRepository, never()).save(any());
        verify(anfitriaoRepository, times(1)).existsByCpf(dto.cpf());
        verify(passwordEncoder, never()).encode(any());
        verify(publisher, never()).publishEvent(any(UsuarioAnfitriaoCriado.class));
    }

    @Test
    @DisplayName("Editar Anfitriao Teste com Retorno Positivo")
    public void editarAnfitriaoTesteRetornoPositivo () {
        Long id = 1L;
        Anfitriao anfitriao = new Anfitriao(id, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false);
        AnfitriaoPostDTO anfitriaoUpdate = new AnfitriaoPostDTO("Teste1", "511.995.364-25",
                "teste1email@email.com", "senhateste1");

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.of(anfitriao));
        when(anfitriaoRepository.save(any())).thenReturn(anfitriao);

        AnfitriaoPutDTO anfitriaoEdit = anfitriaoService.editarAnfitriao(id, anfitriaoUpdate);
        verify(anfitriaoRepository, times(1)).findById(id);
        verify(passwordEncoder, times(1)).encode(any());
        verify(anfitriaoRepository, times(1)).save(anfitriaoCaptor.capture());
        Anfitriao capturado = anfitriaoCaptor.getValue();

        assertThat(anfitriaoEdit).isNotNull();
        assertThat(anfitriaoEdit.id()).isEqualTo(id);
        assertThat(anfitriaoEdit.nome()).isEqualTo("Teste1");
        assertThat(anfitriaoEdit.email()).isEqualTo("teste1email@email.com");
        assertThat(anfitriaoEdit.senha()).isNotEqualTo("senhateste1");
        assertThat(capturado).isNotNull();
    }

    @Test
    @DisplayName("Editar Anfitriao Teste com Retorno Negativo")
    public void editarAnfitriaoTesteRetornoNegativo () {
        Long id = -1L;

        AnfitriaoPostDTO anfitriaoUpdate = new AnfitriaoPostDTO("Teste1", "511.995.364-25",
                "teste1email@email.com", "senhateste1");

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anfitriaoService.editarAnfitriao(id, anfitriaoUpdate)).
                isInstanceOf(UsuarioIDNaoEncontrado.class);
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
        verify(publisher, times(1)).publishEvent(any(AnfitriaoExcluido.class));
    }

    @Test
    @DisplayName("Remover Anfitriao Teste com Retorno Negativo")
    public void removerAnfitriaoTesteRetornoNegativo () {
        Long id = -1L;

        when(anfitriaoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anfitriaoService.excluir(id)).isInstanceOf(UsuarioIDNaoEncontrado.class);

        verify(anfitriaoRepository, times(1)).findById(id);
        verify(anfitriaoRepository, never()).deleteById(id);
        verify(publisher, never()).publishEvent(any(AnfitriaoExcluido.class));
    }

    @Test
    @DisplayName("Get Anfitriao Interno Retorno Positivo")
    public void getAnfitriaoInternoRetornoPositivo() {
        Anfitriao anfitriaoTeste = new Anfitriao(1L, "Teste", "511.995.364-25", "testeemail.com",
                "senhateste", false);

        when(anfitriaoRepository.findById(1L)).thenReturn(Optional.of(anfitriaoTeste));

        UsuarioDTO resultado = anfitriaoService.obterUsuario(1L);

        assertThat(resultado.nome()).isEqualTo("Teste");
        assertThat(resultado.email()).isEqualTo("testeemail.com");
    }

    @Test
    @DisplayName("Get Anfitriao Interno Retorno Negativo")
    public void getAnfitriaoInternoRetornoNegativo() {
        when(anfitriaoRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anfitriaoService.obterUsuario(-1L)).isInstanceOf(UsuarioIDNaoEncontrado.class);
        verify(anfitriaoRepository, times(1)).findById(-1L);
    }
}