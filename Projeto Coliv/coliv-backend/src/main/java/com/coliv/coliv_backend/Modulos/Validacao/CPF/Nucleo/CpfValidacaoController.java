package com.coliv.coliv_backend.Modulos.Validacao.CPF.Nucleo;

import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfRequestDTO;
import com.coliv.coliv_backend.Modulos.Validacao.CPF.Contratos.CpfResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validacao/cpf")
@CrossOrigin("*")
class CpfValidacaoController {

    @Autowired
    private CpfValidacaoService cvs;

    @PostMapping("/validar")
    public CpfResponseDTO validar(@RequestBody CpfRequestDTO dto) {
        return cvs.validar(dto);
    }
}
