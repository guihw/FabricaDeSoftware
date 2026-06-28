package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.CriarPagamentoPlusRequest;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.IAbacatePay;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.PixResponse;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacatePixData;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacatePixQrCodeRequest;
import com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo.dto.AbacatePixQrCodeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
        AbacatePixQrCodeRequest corpo = new AbacatePixQrCodeRequest(
                request.valorEmCentavos(),
                EXPIRACAO_PIX_SEGUNDOS,
                request.descricao()
        );

        try {
            AbacatePixQrCodeResponse resposta = restClient.post()
                    .uri("/pixQrCode/create")
                    .body(corpo)
                    .retrieve()
                    .body(AbacatePixQrCodeResponse.class);

            if (resposta == null || resposta.data() == null) {
                throw new IllegalStateException(
                        "Resposta vazia da AbacatePay ao criar pagamento PIX."
                );
            }

            AbacatePixData data = resposta.data();

            return new PixResponse(
                    data.id(),
                    data.brCodeBase64(),
                    data.brCode(),
                    StatusPagamento.PENDENTE
            );
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(
                    "Erro ao chamar a AbacatePay: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e
            );
        }
    }
}
