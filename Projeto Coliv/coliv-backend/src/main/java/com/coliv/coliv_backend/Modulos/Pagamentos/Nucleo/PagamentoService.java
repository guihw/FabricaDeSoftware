package com.coliv.coliv_backend.Modulos.Pagamentos.Nucleo;

import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.CriarPagamentoPlusRequest;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.IAbacatePay;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.IPagamento;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.PagamentoNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.PixResponse;
import com.coliv.coliv_backend.Modulos.Pagamentos.Contratos.StatusPagamentoResponse;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService implements IPagamento {

    private static final String TIPO_COLEGA = "COLEGA";

    private final IAbacatePay abacatePay;
    private final PagamentoRepository pagamentoRepository;
    private final IAnfitriao anfitriaoService;
    private final IColega colegaService;

    public PagamentoService(
            IAbacatePay abacatePay,
            PagamentoRepository pagamentoRepository,
            IAnfitriao anfitriaoService,
            IColega colegaService
    ) {
        this.abacatePay = abacatePay;
        this.pagamentoRepository = pagamentoRepository;
        this.anfitriaoService = anfitriaoService;
        this.colegaService = colegaService;
    }

    @Override
    public PixResponse criarPagamentoPlus(Long usuarioId, String tipoUsuario) {
        CriarPagamentoPlusRequest request = CriarPagamentoPlusRequest.paraUsuario(usuarioId);

        PixResponse resposta = abacatePay.criarPagamentoPlus(request);

        Pagamento pagamento = new Pagamento(usuarioId, tipoUsuario);
        pagamento.setBillingId(resposta.billingId());
        pagamentoRepository.save(pagamento);

        return resposta;
    }

    /**
     * Consulta o status atual de um pagamento pelo billingId.
     * Garante que o pagamento pertence ao usuário autenticado que está
     * perguntando — sem isso, qualquer usuário logado poderia espiar o
     * status do PIX de outra pessoa só sabendo o billingId.
     */
    public StatusPagamentoResponse consultarStatus(String billingId, Long usuarioIdAutenticado) {
        Pagamento pagamento = pagamentoRepository.findByBillingId(billingId)
                .orElseThrow(() -> new PagamentoNaoEncontrado(billingId));

        if (!pagamento.getUsuarioId().equals(usuarioIdAutenticado)) {
            throw new AccessDeniedException("Este pagamento não pertence ao usuário autenticado.");
        }

        return new StatusPagamentoResponse(pagamento.getBillingId(), pagamento.getStatus());
    }

    /**
     * Chamado pelo AbacatePayWebhookController quando a AbacatePay confirma
     * o pagamento. Atualiza o status e, se PAGO, ativa o plano do usuário
     * correto (Anfitriao ou Colega, conforme o que foi salvo na criação).
     */
    public void atualizarStatusPorBillingId(String billingId, StatusPagamento novoStatus) {
        pagamentoRepository.findByBillingId(billingId).ifPresent(pagamento -> {
            pagamento.setStatus(novoStatus);
            pagamentoRepository.save(pagamento);

            if (novoStatus == StatusPagamento.PAGO) {
                ativarPlano(pagamento.getUsuarioId(), pagamento.getTipoUsuario());
            }
        });
    }

    private void ativarPlano(Long usuarioId, String tipoUsuario) {
        if (TIPO_COLEGA.equals(tipoUsuario)) {
            colegaService.ativarPlano(usuarioId);
        } else {
            anfitriaoService.ativarPlano(usuarioId);
        }
    }
}
