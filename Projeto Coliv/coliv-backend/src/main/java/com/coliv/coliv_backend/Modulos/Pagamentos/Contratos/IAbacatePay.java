package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

public interface IAbacatePay {
    PixResponse criarPagamentoPlus(CriarPagamentoPlusRequest request);
}
