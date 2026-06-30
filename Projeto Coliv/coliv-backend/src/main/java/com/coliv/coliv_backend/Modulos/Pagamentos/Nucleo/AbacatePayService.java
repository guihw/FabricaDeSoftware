package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.CriarPagamentoPlusRequest;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.IAbacatePay;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.PixResponse;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacateTransparentData;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacateTransparentRequest;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacateTransparentResponse;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacateTransparentResultData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class AbacatePayService implements IAbacatePay {

    private static final int EXPIRACAO_PIX_SEGUNDOS = 3600; // 1 hora

    private final RestClient restClient;

    public AbacatePayService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PixResponse criarPagamentoPlus(CriarPagamentoPlusRequest request) {
        AbacateTransparentData dados = new AbacateTransparentData(
                request.valorEmCentavos(),
                EXPIRACAO_PIX_SEGUNDOS,
                request.descricao()
        );

        AbacateTransparentRequest corpo = AbacateTransparentRequest.pix(dados);

        try {
            AbacateTransparentResponse resposta = restClient.post()
                    .uri("/v2/transparents/create")
                    .body(corpo)
                    .retrieve()
                    .body(AbacateTransparentResponse.class);

            if (resposta == null || resposta.data() == null) {
                throw new IllegalStateException(
                        "Resposta vazia da AbacatePay ao criar pagamento PIX."
                );
            }

            AbacateTransparentResultData data = resposta.data();

            return new PixResponse(
                    data.id(),
                    data.brCodeBase64(),
                    data.brCode(),
                    StatusPagamento.PENDENTE
            );
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(
                    "Serviço de pagamento retornou erro: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e
            );
        } catch (RestClientException e) {
            throw new IllegalStateException(
                    "Não foi possível conectar ao serviço de pagamento. Tente novamente em instantes.",
                    e
            );
        }
    }
}
