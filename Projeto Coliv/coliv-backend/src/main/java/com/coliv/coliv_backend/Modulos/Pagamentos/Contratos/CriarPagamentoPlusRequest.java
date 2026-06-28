package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

/**
 * Dados necessários para criar uma cobrança PIX do plano Plus.
 * Não enviamos o objeto "customer" da AbacatePay (é opcional na API e,
 * se enviado, exige nome+telefone+email+cpf juntos) — por isso pedimos
 * apenas o valor e a descrição, que já são fixos para o plano Plus.
 */
public record CriarPagamentoPlusRequest(
        Long usuarioId,
        Integer valorEmCentavos,
        String descricao
) {
    private static final int VALOR_PLANO_PLUS_CENTAVOS = 2990; // R$ 29,90 - ajuste para o valor real do seu plano
    private static final String DESCRICAO_PLANO_PLUS = "Assinatura Coliv Plus";

    public static CriarPagamentoPlusRequest paraUsuario(Long usuarioId) {
        return new CriarPagamentoPlusRequest(
                usuarioId,
                VALOR_PLANO_PLUS_CENTAVOS,
                DESCRICAO_PLANO_PLUS
        );
    }
}
