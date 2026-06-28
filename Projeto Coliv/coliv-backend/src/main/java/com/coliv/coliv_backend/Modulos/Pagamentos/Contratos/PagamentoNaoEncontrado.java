package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

public class PagamentoNaoEncontrado extends RuntimeException {
    public PagamentoNaoEncontrado(Long id) {
        super("Pagamento não encontrado. Id: " + id);
    }

    public PagamentoNaoEncontrado(String billingId) {
        super("Pagamento não encontrado. BillingId: " + billingId);
    }
}
