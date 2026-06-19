package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.*;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenciasColegaService implements IPreferenciasColega {

    private final PreferenciasColegaRepository repository;

    public PreferenciasColegaService(PreferenciasColegaRepository repository) {
        this.repository = repository;
    }

    public List<PreferenciasColegaResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PreferenciasColegaResponse buscarPorId(Long id) {
        PreferenciasColega preferencias = repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));
        return mapToResponse(preferencias);
    }

    @Transactional
    public PreferenciasColegaResponse criarPreferencia(Long colegaId, PreferenciasColegaDTO dto) {

        if (dto.precoMinimo().compareTo(dto.precoMaximo()) > 0) {
            throw new RuntimeException("Preço mínimo não pode ser maior que o máximo");
        }

        PreferenciasColega preferencias = repository.findByColegaId(colegaId)
                .orElseGet(() -> new PreferenciasColega.Builder().colegaId(colegaId).build());

        preferencias.setPrecoMinimo(dto.precoMinimo());
        preferencias.setPrecoMaximo(dto.precoMaximo());
        preferencias.setLocalizacao(dto.localizacao());
        preferencias.setHorarioDeSono(dto.horarioDeSono());
        preferencias.setNivelDeSociabilidade(dto.nivelDeSociabilidade());
        preferencias.setNivelDeLimpeza(dto.nivelDeLimpeza());
        preferencias.setHabitoDeTrabalho(dto.habitoDeTrabalho());
        preferencias.setAceitaAnimais(dto.aceitaAnimais());

        return mapToResponse(repository.save(preferencias));
    }

    @Transactional
    public PreferenciasColegaResponse editarPreferencias(Long id, PreferenciasColegaDTO dto) {

        PreferenciasColega original = repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));

        if (dto.precoMinimo() != null)  original.setPrecoMinimo(dto.precoMinimo());
        if (dto.precoMaximo() != null)  original.setPrecoMaximo(dto.precoMaximo());
        if (dto.localizacao() != null && !dto.localizacao().isBlank()) original.setLocalizacao(dto.localizacao());
        if (dto.horarioDeSono() != null) original.setHorarioDeSono(dto.horarioDeSono());
        if (dto.nivelDeSociabilidade() != null) original.setNivelDeSociabilidade(dto.nivelDeSociabilidade());
        if (dto.nivelDeLimpeza() != null) original.setNivelDeLimpeza(dto.nivelDeLimpeza());
        if (dto.habitoDeTrabalho() != null) original.setHabitoDeTrabalho(dto.habitoDeTrabalho());
        original.setAceitaAnimais(dto.aceitaAnimais());

        return mapToResponse(repository.save(original));
    }

    @Transactional
    public void excluir(Long id) {
        repository.findById(id).orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));
        repository.deleteById(id);
    }

    @Override
    public PreferenciasColegaResponse getPreferenciasColega(Long colegaId) {
        PreferenciasColega preferencias = repository.findByColegaId(colegaId)
                .orElseThrow(() -> new PreferenciaColegaNaoEncontradaUsandoReferencia(colegaId));
        return mapToResponse(preferencias);
    }

    @EventListener
    public void eventoColegaCriado(com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.UsuarioColegaCriado evento) {
        // Só cria se ainda não existir (idempotente)
        if (repository.findByColegaId(evento.colegaId()).isEmpty()) {
            PreferenciasColega preferencias = new PreferenciasColega.Builder()
                    .colegaId(evento.colegaId())
                    .build();
            repository.save(preferencias);
        }
    }

    @EventListener
    public void eventoColegaExcluido(ColegaExcluido evento) {
        PreferenciasColega preferencias = repository.findByColegaId(evento.colegaId())
                .orElseThrow(() -> new PreferenciaColegaNaoEncontradaUsandoReferencia(evento.colegaId()));
        repository.deleteById(preferencias.getId());
    }

    private PreferenciasColegaResponse mapToResponse(PreferenciasColega p) {
        return new PreferenciasColegaResponse(
                p.getId(),
                p.getPrecoMinimo(),
                p.getPrecoMaximo(),
                p.getLocalizacao(),
                p.getHorarioDeSono(),
                p.getNivelDeSociabilidade(),
                p.getNivelDeLimpeza(),
                p.getHabitoDeTrabalho(),
                p.isAceitaAnimais()
        );
    }
}