package com.coliv.coliv_backend.Modulos.Validacao.CPF.Nucleo;

import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfRequestDTO;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Validação de CPF", description = "Valida se um CPF é válido antes do cadastro. Endpoint público.")
@RestController
@RequestMapping("/validacao/cpf")
@CrossOrigin("*")
class CpfValidacaoController {

    @Autowired
    private CpfValidacaoService cvs;

    @Operation(summary = "Validar CPF", description = "Verifica se o CPF fornecido é matematicamente válido")
    @SecurityRequirements
    @PostMapping("/validar")
    public CpfResponseDTO validar(@RequestBody CpfRequestDTO dto) {
        return cvs.validar(dto);
    }
}
