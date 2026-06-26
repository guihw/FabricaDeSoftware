package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    @Autowired
    private IAbacatePay abacatePay;

    public PixResponse criarPix(
            CriarPixRequest request
    ) {

        return abacatePay.criarPix(request);

    }

}