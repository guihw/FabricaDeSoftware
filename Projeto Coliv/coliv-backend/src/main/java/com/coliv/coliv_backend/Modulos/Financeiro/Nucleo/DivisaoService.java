package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoDTO;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoEditada;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.DespesaAdicionadaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class DivisaoService {

    @Autowired
    private DivisaoRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;

    public Divisao criar(DivisaoDTO dto) {

        Divisao divisao = new Divisao();

        divisao.setDespesaId(dto.despesaId());
        divisao.setUsuarioId(dto.usuarioId());
        divisao.setArquivoId(dto.arquivoId());
        divisao.setValor(dto.valor());

        Divisao salva = repository.save(divisao);

        publisher.publishEvent(new DespesaAdicionadaEvent(salva.getUsuarioId(), salva.getDespesaId(), salva.getValor()));

        return salva;
    }

    public List<Divisao> listar() {
        return repository.findAll();
    }

    public Divisao buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new DivisaoIdNaoEncontrado(id));
    }

    public Divisao editar(Long id, DivisaoDTO dto) {

        Divisao divisao = buscarPorId(id);

        divisao.setDespesaId(dto.despesaId());
        divisao.setUsuarioId(dto.usuarioId());
        divisao.setArquivoId(dto.arquivoId());
        divisao.setValor(dto.valor());

        Divisao salva = repository.save(divisao);

        publisher.publishEvent(
                new DivisaoEditada(
                        salva.getId(),
                        salva.getDespesaId(),
                        salva.getUsuarioId(),
                        salva.getValor()
                )
        );

        return salva;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        repository.deleteById(id);
    }
}