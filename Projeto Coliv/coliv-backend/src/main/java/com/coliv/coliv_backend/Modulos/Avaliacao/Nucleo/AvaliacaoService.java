package com.coliv.coliv_backend.Modulos.Avaliacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Avaliacao.Contratos.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class AvaliacaoService implements IAvaliacao {

    private final AvaliacaoRepository repository;

    AvaliacaoService(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    public Avaliacao criar(AvaliacaoDTO dto) {

        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setCardId(dto.cardId());
        avaliacao.setUsuarioId(dto.usuarioId());
        avaliacao.setValorClassificacao(dto.valorClassificacao());

        return repository.save(avaliacao);
    }

    public List<Avaliacao> listar() {
        return repository.findAll();
    }

    public Avaliacao buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new AvaliacaoIdNaoEncontrado(id));
    }

    public Avaliacao editar(Long id,
                            AvaliacaoDTO dto) {

        Avaliacao avaliacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new AvaliacaoIdNaoEncontrado(id));

        avaliacao.setCardId(dto.cardId());
        avaliacao.setUsuarioId(dto.usuarioId());
        avaliacao.setValorClassificacao(dto.valorClassificacao());

        return repository.save(avaliacao);
    }

    public void excluir(Long id) {

        repository.findById(id)
                .orElseThrow(() ->
                        new AvaliacaoIdNaoEncontrado(id));

        repository.deleteById(id);
    }

    @Override
    public AvaliacaoResponse getAvaliacao(Long id) {

        Avaliacao avaliacao =
                repository.findById(id)
                        .orElseThrow(() ->
                                new AvaliacaoIdNaoEncontrado(id));

        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getCardId(),
                avaliacao.getUsuarioId(),
                avaliacao.getValorClassificacao()
        );
    }
}