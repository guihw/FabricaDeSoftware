package com.coliv.coliv_backend.Modulos.Arquivos.Contratos;

import java.util.List;

public interface IArquivos {

    List<ArquivoDTO> getArquivos(List<Long> idList);

    ArquivoDTO getArquivo(Long id);
}