package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos;

public class DadosImovelIDNaoEncontrado extends RuntimeException {
    public DadosImovelIDNaoEncontrado(Long id) {
        super("Dados do Imóvel de Id " + id + " não encontrada . . .");
    }
}
