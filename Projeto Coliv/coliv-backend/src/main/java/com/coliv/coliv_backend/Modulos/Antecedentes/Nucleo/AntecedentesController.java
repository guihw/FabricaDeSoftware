package com.coliv.coliv_backend.Modulos.Antecedentes.Nucleo;

import com.coliv.coliv_backend.Modulos.Antecedentes.Contratos.ResultadoAntecedentesDTO;
import com.coliv.coliv_backend.Modulos.Antecedentes.Contratos.VerificacaoAntecedentesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Antecedentes Criminais", description = "Verificação de antecedentes criminais de anfitriões e colegas")
@RestController
@RequestMapping("/antecedentes")
@CrossOrigin(origins = "*")
class AntecedentesController {

    @Autowired
    private AntecedentesService antecedentesService;

    @Operation(
            summary = "Verificar antecedentes criminais",
            description = "Consulta os antecedentes criminais de um usuário (anfitrião ou colega) nas bases " +
                    "BNMP, INFOSEG e TJ estaduais. Requer autenticação."
    )
    @PostMapping("/verificar")
    public ResultadoAntecedentesDTO verificar(@RequestBody VerificacaoAntecedentesDTO dto) {
        return antecedentesService.verificar(dto);
    }
}
