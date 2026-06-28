package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class DespesaService implements IFinanceiro {

    @Autowired
    private DespesaRepository repository;

    @Autowired
    private DivisaoRepository divisaoRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    public Despesa criar(DespesaDTO dto) {

        Despesa despesa = new Despesa();

        despesa.setValor(dto.valor());
        despesa.setDescricao(dto.descricao());
        despesa.setDataVencimento(dto.dataVencimento());

        Despesa salva = repository.save(despesa);

        publisher.publishEvent(
                new NovaDespesa(
                        salva.getId(),
                        salva.getValor(),
                        salva.getDescricao()
                )
        );

        return salva;
    }

    public List<Despesa> listar() {
        return repository.findAll();
    }

    public Despesa buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new DespesaIdNaoEncontrado(id));
    }

    public Despesa editar(Long id, DespesaDTO dto) {

        Despesa despesa = buscarPorId(id);

        despesa.setValor(dto.valor());
        despesa.setDescricao(dto.descricao());
        despesa.setDataVencimento(dto.dataVencimento());

        return repository.save(despesa);
    }

    public void excluir(Long id) {

        buscarPorId(id);

        repository.deleteById(id);
    }

    @Override
    public DespesaResponse getDespesa(Long id) {

        Despesa despesa = buscarPorId(id);

        return new DespesaResponse(
                despesa.getId(),
                despesa.getValor(),
                despesa.getDataVencimento(),
                despesa.getDescricao(),
                despesa.getPago()
        );
    }

    @Override
    public DivisaoResponse getDivisao(Long id) {

        Divisao divisao = divisaoRepository.findById(id)
                .orElseThrow(() ->
                        new DivisaoIdNaoEncontrado(id));

        return new DivisaoResponse(
                divisao.getId(),
                divisao.getDespesaId(),
                divisao.getUsuarioId(),
                divisao.getArquivoId(),
                divisao.getValor()
        );
    }

    @Override
    public List<DivisaoResponse> getDivisoes(Long despesaId) {

        return divisaoRepository.findByDespesaId(despesaId)
                .stream()
                .map(divisao ->
                        new DivisaoResponse(
                                divisao.getId(),
                                divisao.getDespesaId(),
                                divisao.getUsuarioId(),
                                divisao.getArquivoId(),
                                divisao.getValor()
                        ))
                .toList();
    }

    public Despesa marcarComoPago(Long id, Long usuarioId) {
        Despesa despesa = buscarPorId(id);

        if (!despesa.getPago().contains(usuarioId)) {
            despesa.getPago().add(usuarioId);
        }

        return repository.save(despesa);
    }

    public Despesa desmarcarComoPago(Long id, Long usuarioId) {
        Despesa despesa = buscarPorId(id);

        despesa.getPago().remove(usuarioId);

        return repository.save(despesa);
    }
}