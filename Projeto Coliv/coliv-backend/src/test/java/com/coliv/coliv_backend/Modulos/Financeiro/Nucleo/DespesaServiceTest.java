package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.IChatMembros;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DespesaDTO;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DespesaIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.NovaDespesa;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DespesaServiceTest {

    @Mock
    private DespesaRepository repository;

    @Mock
    private DivisaoRepository divisaoRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private IChatMembros chatMembros;

    @InjectMocks
    private DespesaService service;

    private Despesa despesa;

    private static final Long ANFITRIAO_ID = 99L;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setup() {

        despesa = new Despesa();

        despesa.setId(1L);
        despesa.setValor(100.0);
        despesa.setDescricao("Internet");
        despesa.setDataVencimento(LocalDateTime.now());
        despesa.setAnfitriaoId(ANFITRIAO_ID);

        UsuarioAutenticado usuario =
                new UsuarioAutenticado(1L, "usuario@teste.com", "hash", "ANFITRIAO");

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(usuario);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        lenient().when(chatMembros.usuarioPertenceAoAnfitriao(anyLong(), anyLong()))
                .thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    void deveCriarDespesa() {

        DespesaDTO dto =
                new DespesaDTO(
                        100.0,
                        LocalDateTime.now(),
                        "Internet",
                        ANFITRIAO_ID
                );

        when(repository.save(any(Despesa.class)))
                .thenReturn(despesa);

        Despesa resultado =
                service.criar(dto);

        assertNotNull(resultado);
        assertEquals(100.0, resultado.getValor());

        verify(repository).save(any(Despesa.class));
        verify(publisher).publishEvent(any(NovaDespesa.class));
    }

    @Test
    void deveListarDespesasPorAnfitriao() {

        when(repository.findAllByAnfitriaoId(ANFITRIAO_ID))
                .thenReturn(List.of(despesa));

        List<Despesa> lista =
                service.listarPorAnfitriao(ANFITRIAO_ID);

        assertEquals(1, lista.size());
    }

    @Test
    void deveBuscarPorId() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(despesa));

        Despesa resultado =
                service.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrar() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                DespesaIdNaoEncontrado.class,
                () -> service.buscarPorId(1L)
        );
    }

    @Test
    void deveEditarDespesa() {

        DespesaDTO dto =
                new DespesaDTO(
                        250.0,
                        LocalDateTime.now(),
                        "Energia",
                        ANFITRIAO_ID
                );

        when(repository.findById(1L))
                .thenReturn(Optional.of(despesa));

        when(repository.save(any(Despesa.class)))
                .thenReturn(despesa);

        Despesa resultado =
                service.editar(1L, dto);

        assertNotNull(resultado);

        verify(repository).save(any(Despesa.class));
    }

    @Test
    void deveExcluirDespesa() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(despesa));

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}
