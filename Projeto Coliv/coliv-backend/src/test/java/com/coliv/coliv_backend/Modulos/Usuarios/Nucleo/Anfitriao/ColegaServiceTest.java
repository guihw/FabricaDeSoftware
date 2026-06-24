package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.ColegaResponse;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.CreateColegaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ColegaServiceTest {

    @Mock
    private ColegaRepository colegaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao iAnfitriao;

    @InjectMocks
    private ColegaService colegaService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        org.springframework.test.util.ReflectionTestUtils.setField(colegaService, "iAnfitriao", iAnfitriao);
    }

    @Test
    void deveCriarColegaComSucesso() {

        CreateColegaRequest request = new CreateColegaRequest(
                "Fernando",
                "fernando@email.com",
                "123456",
                "01226588914"
        );

        when(colegaRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("senhaCriptografada");

        Colega colegaSalvo = new Colega();

        colegaSalvo.setId(1L);
        colegaSalvo.setNome(request.nome());
        colegaSalvo.setEmail(request.email());
        colegaSalvo.setSenha("senhaCriptografada");

        when(colegaRepository.save(any(Colega.class)))
                .thenReturn(colegaSalvo);

        ColegaResponse response = colegaService.createColega(request);

        assertNotNull(response);

        assertEquals(1L, response.id());
        assertEquals("Fernando", response.nome());
        assertEquals("fernando@email.com", response.email());

        verify(colegaRepository, times(1))
                .save(any(Colega.class));
    }

    @Test
    void deveLancarErroQuandoEmailJaExiste() {

        CreateColegaRequest request = new CreateColegaRequest(
                "Fernando",
                "fernando@email.com",
                "123456",
                "01226588914"
        );

        when(colegaRepository.existsByEmail(request.email()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> colegaService.createColega(request)
        );

        assertEquals("Email já registrado", exception.getMessage());

        verify(colegaRepository, never())
                .save(any(Colega.class));
    }

    @Test
    void deveLancarErroQuandoSenhaForMenorQue6Caracteres() {

        CreateColegaRequest request = new CreateColegaRequest(
                "Fernando",
                "fernando@email.com",
                "123",
                "01226588914"
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> colegaService.createColega(request)
        );

        assertEquals(
                "Password precisa ter 6 caracteres",
                exception.getMessage()
        );

        verify(colegaRepository, never())
                .save(any(Colega.class));
    }
}