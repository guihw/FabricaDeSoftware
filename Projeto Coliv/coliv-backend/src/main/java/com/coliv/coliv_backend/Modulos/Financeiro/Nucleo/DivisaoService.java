package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.IChatMembros;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.AcessoNegadoDespesa;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DespesaIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoDTO;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoEditada;
import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DivisaoIdNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Notificacao.Contratos.DespesaAdicionadaEvent;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class DivisaoService {

    @Autowired
    private DivisaoRepository repository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private IChatMembros chatMembros;

    @Autowired
    private ApplicationEventPublisher publisher;

    private void verificarAcessoADespesa(Long despesaId) {
        Despesa despesa = despesaRepository.findById(despesaId)
                .orElseThrow(() -> new DespesaIdNaoEncontrado(despesaId));

        UsuarioAutenticado usuario = (UsuarioAutenticado) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!chatMembros.usuarioPertenceAoAnfitriao(usuario.getId(), despesa.getAnfitriaoId())) {
            throw new AcessoNegadoDespesa();
        }
    }

    public Divisao criar(DivisaoDTO dto) {

        verificarAcessoADespesa(dto.despesaId());

        Divisao divisao = new Divisao();

        divisao.setDespesaId(dto.despesaId());
        divisao.setUsuarioId(dto.usuarioId());
        divisao.setArquivoId(dto.arquivoId());
        divisao.setValor(dto.valor());

        Divisao salva = repository.save(divisao);

        publisher.publishEvent(new DespesaAdicionadaEvent(salva.getUsuarioId(), salva.getDespesaId(), salva.getValor()));

        return salva;
    }

    public Divisao buscarPorId(Long id) {

        Divisao divisao = repository.findById(id)
                .orElseThrow(() ->
                        new DivisaoIdNaoEncontrado(id));

        verificarAcessoADespesa(divisao.getDespesaId());

        return divisao;
    }

    public Divisao editar(Long id, DivisaoDTO dto) {

        Divisao divisao = buscarPorId(id);

        verificarAcessoADespesa(dto.despesaId());

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