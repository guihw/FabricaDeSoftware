package com.coliv.coliv_backend.Modulos.Validacao.CPF.Nucleo;

import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class CpfValidacaoExceptionHandler {

    @ExceptionHandler(CpfInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCpfInvalido(CpfInvalidoException ex) {
        return Map.of("erro", ex.getMessage());
    }
}
