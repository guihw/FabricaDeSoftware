package com.coliv.coliv_backend.Modulos.Financeiro.Contratos;

import java.util.List;

public interface IFinanceiro {

    DespesaResponse getDespesa(Long id);

    DivisaoResponse getDivisao(Long id);

    List<DivisaoResponse> getDivisoes(Long despesaId);
}