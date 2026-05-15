package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos;

public class DadosImovelNaoEncontradoUsandoReferencia extends RuntimeException {
    public DadosImovelNaoEncontradoUsandoReferencia(Long id) {
        super("Nenhuma Dado de Imovel encontrado utilizando o id " + id + " como Referencia");
    }
}
