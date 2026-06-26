package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin("*")
public class PagamentoController {

    @Autowired
    private PagamentoService service;

    @PostMapping("/plus")
    public PixResponse criarPix(
            @RequestBody CriarPixRequest request
    ) {

        return service.criarPix(request);

    }

}