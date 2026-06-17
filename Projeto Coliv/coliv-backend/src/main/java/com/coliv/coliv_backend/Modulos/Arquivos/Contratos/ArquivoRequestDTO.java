package com.coliv.coliv_backend.Modulos.Arquivos.Contratos;

import com.coliv.coliv_backend.Modulos.Arquivos.Nucleo.TipoArquivo;

public record ArquivoRequestDTO(
        String url,
        TipoArquivo type
) {
}