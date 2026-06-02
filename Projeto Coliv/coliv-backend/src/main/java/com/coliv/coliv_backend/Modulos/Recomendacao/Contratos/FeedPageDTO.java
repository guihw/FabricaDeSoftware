package com.coliv.coliv_backend.Modulos.Recomendacao.Contratos;

import java.util.List;

public record FeedPageDTO<T>(List<T> itens,
                             int pagina,
                             int tamanhoPagina,
                             long totalItens,
                             boolean temProxima) {
}
