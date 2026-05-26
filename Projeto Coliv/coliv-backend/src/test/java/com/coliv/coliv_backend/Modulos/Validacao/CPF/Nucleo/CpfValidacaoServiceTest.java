package com.coliv.coliv_backend.Modulos.Validacao.CPF.Nucleo;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfInvalidoException;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfRequestDTO;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;



@ExtendWith(MockitoExtension.class)
public class CpfValidacaoServiceTest {

    @InjectMocks
    private CpfValidacaoService cvs;

    @Test
    @DisplayName("CPF válido retorna true")
    public void cpfValidoRetornaTrue() {
        CpfRequestDTO dto = new CpfRequestDTO("111.444.777-35");

        CpfResponseDTO resultado = cvs.validar(dto);

        assertThat(resultado.valido()).isTrue();
        assertThat(resultado.mensagem()).isEqualTo("CPF válido");
    }

    @Test
    @DisplayName("CPF com dígito errado retorna false")
    public void cpfComDigitoErradoRetornaFalse() {
        CpfRequestDTO dto = new CpfRequestDTO("111.444.777-00");

        CpfResponseDTO resultado = cvs.validar(dto);

        assertThat(resultado.valido()).isFalse();
    }

    @Test
    @DisplayName("CPF com todos dígitos iguais retorna false")
    public void cpfComDigitosIguaisRetornaFalse() {
        CpfRequestDTO dto = new CpfRequestDTO("111.111.111-11");

        CpfResponseDTO resultado = cvs.validar(dto);

        assertThat(resultado.valido()).isFalse();
    }

    @Test
    @DisplayName("CPF com formato impossível lança exceção")
    public void cpfComFormatoImpossivelLancaExcecao() {
        CpfRequestDTO dto = new CpfRequestDTO("abc");

        assertThatThrownBy(() -> cvs.validar(dto))
                .isInstanceOf(CpfInvalidoException.class);
    }
}
