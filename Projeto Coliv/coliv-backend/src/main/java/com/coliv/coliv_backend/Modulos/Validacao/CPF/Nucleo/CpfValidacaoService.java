package com.coliv.coliv_backend.Modulos.Validacao.CPF.Nucleo;

import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfInvalidoException;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfRequestDTO;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfResponseDTO;
import org.springframework.stereotype.Service;

@Service
class CpfValidacaoService {

    private static final int[] PESOS_PRIMEIRO_DIGITO  = {10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_SEGUNDO_DIGITO = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    public CpfResponseDTO validar(CpfRequestDTO dto) {
        String cpfLimpo = limpar(dto.cpf());

        if (!formatoBasicoValido(cpfLimpo)) {
            throw new CpfInvalidoException(dto.cpf());
        }

        if (todosDigitosIguais(cpfLimpo)) {
            return new CpfResponseDTO(dto.cpf(), false, "CPF inválido: dígitos todos iguais");
        }

        boolean primeiroDigitoValido  = validarDigito(cpfLimpo, PESOS_PRIMEIRO_DIGITO, 9);
        boolean segundoDigitoValido = validarDigito(cpfLimpo, PESOS_SEGUNDO_DIGITO, 10);

        if (primeiroDigitoValido && segundoDigitoValido) {
            return new CpfResponseDTO(dto.cpf(), true, "CPF válido");
        }

        return new CpfResponseDTO(dto.cpf(), false, "CPF inválido: dígito verificador incorreto");
    }

    private String limpar(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }

    private boolean formatoBasicoValido(String cpf) {
        return cpf != null && cpf.length() == 11 && cpf.matches("\\d{11}");
    }

    private boolean todosDigitosIguais(String cpf) {
        return cpf.chars().distinct().count() == 1;
    }

    private boolean validarDigito(String cpf, int[] pesos, int posicaoDigito) {
        int soma = 0;

        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * pesos[i];
        }

        int resto = (soma * 10) % 11;
        int digitoEsperado = (resto == 10 || resto == 11) ? 0 : resto;
        int digitoInformado = Character.getNumericValue(cpf.charAt(posicaoDigito));

        return digitoEsperado == digitoInformado;
    }
}
