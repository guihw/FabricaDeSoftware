package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.IChatMembros;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.*;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private IChatMembros chatMembros;

    public Despesa criar(DespesaDTO dto) {

        verificarAcesso(dto.anfitriaoId());

        Despesa despesa = new Despesa();

        despesa.setValor(dto.valor());
        despesa.setDescricao(dto.descricao());
        despesa.setDataVencimento(dto.dataVencimento());
        despesa.setAnfitriaoId(dto.anfitriaoId());

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

    public List<Despesa> listarPorAnfitriao(Long anfitriaoId) {
        verificarAcesso(anfitriaoId);
        return repository.findAllByAnfitriaoId(anfitriaoId);
    }

    public Despesa buscarPorId(Long id) {

        Despesa despesa = repository.findById(id)
                .orElseThrow(() ->
                        new DespesaIdNaoEncontrado(id));

        verificarAcesso(despesa.getAnfitriaoId());

        return despesa;
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

    private void verificarAcesso(Long anfitriaoId) {
        UsuarioAutenticado usuario = (UsuarioAutenticado) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!chatMembros.usuarioPertenceAoAnfitriao(usuario.getId(), anfitriaoId)) {
            throw new AcessoNegadoDespesa();
        }
    }

    @Override
    public DespesaResponse getDespesa(Long id) {

        Despesa despesa = buscarPorId(id);

        return new DespesaResponse(
                despesa.getId(),
                despesa.getValor(),
                despesa.getDataVencimento(),
                despesa.getDescricao(),
                despesa.getPago(),
                despesa.getAnfitriaoId()
        );
    }

    @Override
    public DivisaoResponse getDivisao(Long id) {

        Divisao divisao = divisaoRepository.findById(id)
                .orElseThrow(() ->
                        new DivisaoIdNaoEncontrado(id));

        buscarPorId(divisao.getDespesaId());

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

        buscarPorId(despesaId);

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