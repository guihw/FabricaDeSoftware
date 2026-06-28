package com.coliv.coliv_backend.Modulos.Pagamentos.Contratos;

public interface IPagamento {
    /**
     * tipoUsuario deve ser "ANFITRIAO" ou "COLEGA" (mesmo valor de
     * UsuarioAutenticado.getTipo()), pois Anfitriao e Colega são entidades
     * JPA separadas, cada uma com seu próprio repositório/serviço.
     */
    PixResponse criarPagamentoPlus(Long usuarioId, String tipoUsuario);
}
