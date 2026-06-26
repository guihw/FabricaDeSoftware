package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.*;
import org.springframework.stereotype.Component;

@Component
public class AbacatePayClient
        implements IAbacatePay {

    @Override
    public PixResponse criarPix(
            CriarPixRequest request
    ) {

        throw new UnsupportedOperationException(
                "Integração será implementada na próxima etapa."
        );

    }

}