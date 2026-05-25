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
                .map(preferencias -> new PreferenciasColegaResponse(
                        preferencias.getId(),
                        preferencias.getPrecoMinimo(),
                        preferencias.getPrecoMaximo(),
                        preferencias.getLocalizacao(),
                        preferencias.getHorarioDeSono(),
                        preferencias.getNivelDeSociabilidade(),
                        preferencias.getNivelDeLimpeza(),
                        preferencias.getHabitoDeTrabalho(),
                        preferencias.isAceitaAnimais()
                ))
                .toList();
    }

    public PreferenciasColegaResponse buscarPorId(Long id) {

        PreferenciasColega preferencias = repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));

        return new PreferenciasColegaResponse(
                preferencias.getId(),
                preferencias.getPrecoMinimo(),
                preferencias.getPrecoMaximo(),
                preferencias.getLocalizacao(),
                preferencias.getHorarioDeSono(),
                preferencias.getNivelDeSociabilidade(),
                preferencias.getNivelDeLimpeza(),
                preferencias.getHabitoDeTrabalho(),
                preferencias.isAceitaAnimais()
        );
    }

    @Transactional
    public PreferenciasColegaResponse criarPreferencia(Long colegaId, PreferenciasColegaDTO dto) {

        if (dto.precoMinimo().compareTo(dto.precoMaximo()) > 0) {
            throw new RuntimeException("Preço mínimo não pode ser maior que o máximo");
        }

        PreferenciasColega preferencias = new PreferenciasColega.Builder()
                .precoMinimo(dto.precoMinimo())
                .precoMaximo(dto.precoMaximo())
                .localizacao(dto.localizacao())
                .horarioDeSono(dto.horarioDeSono())
                .nivelDeSociabilidade(dto.nivelDeSociabilidade())
                .nivelDeLimpeza(dto.nivelDeLimpeza())
                .habitoDeTrabalho(dto.habitoDeTrabalho())
                .aceitaAnimais(dto.aceitaAnimais())
                .colegaId(colegaId)
                .build();

        PreferenciasColega saved = repository.save(preferencias);

        return new PreferenciasColegaResponse(
                saved.getId(),
                saved.getPrecoMinimo(),
                saved.getPrecoMaximo(),
                saved.getLocalizacao(),
                saved.getHorarioDeSono(),
                saved.getNivelDeSociabilidade(),
                saved.getNivelDeLimpeza(),
                saved.getHabitoDeTrabalho(),
                saved.isAceitaAnimais()
        );
    }

    @Transactional
    public PreferenciasColegaResponse editarPreferencias(Long id, PreferenciasColegaDTO dto) {

        PreferenciasColega original = repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));

        if (dto.precoMinimo() != null) {
            original.setPrecoMinimo(dto.precoMinimo());
        }

        if (dto.precoMaximo() != null) {
            original.setPrecoMaximo(dto.precoMaximo());
        }

        if (dto.localizacao() != null && !dto.localizacao().isBlank()) {
            original.setLocalizacao(dto.localizacao());
        }

        if (dto.horarioDeSono() != null) {
            original.setHorarioDeSono(dto.horarioDeSono());
        }

        if (dto.nivelDeSociabilidade() != null) {
            original.setNivelDeSociabilidade(dto.nivelDeSociabilidade());
        }

        if (dto.nivelDeLimpeza() != null) {
            original.setNivelDeLimpeza(dto.nivelDeLimpeza());
        }

        if (dto.habitoDeTrabalho() != null) {
            original.setHabitoDeTrabalho(dto.habitoDeTrabalho());
        }

        original.setAceitaAnimais(dto.aceitaAnimais());

        PreferenciasColega updated = repository.save(original);

        return new PreferenciasColegaResponse(
                updated.getId(),
                updated.getPrecoMinimo(),
                updated.getPrecoMaximo(),
                updated.getLocalizacao(),
                updated.getHorarioDeSono(),
                updated.getNivelDeSociabilidade(),
                updated.getNivelDeLimpeza(),
                updated.getHabitoDeTrabalho(),
                updated.isAceitaAnimais()
        );
    }

    @Transactional
    public void excluir(Long id) {

        repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));

        repository.deleteById(id);
    }

    @Override
    public PreferenciasColegaResponse getPreferenciasColega(Long colegaId) {

        PreferenciasColega preferencias = repository.findByColegaId(colegaId)
                .orElseThrow(() ->
                        new PreferenciaColegaNaoEncontradaUsandoReferencia(colegaId));

        return new PreferenciasColegaResponse(
                preferencias.getId(),
                preferencias.getPrecoMinimo(),
                preferencias.getPrecoMaximo(),
                preferencias.getLocalizacao(),
                preferencias.getHorarioDeSono(),
                preferencias.getNivelDeSociabilidade(),
                preferencias.getNivelDeLimpeza(),
                preferencias.getHabitoDeTrabalho(),
                preferencias.isAceitaAnimais()
        );
    }

    @EventListener
    public void eventoColegaCriado(UsuarioColegaCriado evento) {

        PreferenciasColega preferencias = new PreferenciasColega.Builder()
                .colegaId(evento.colegaId())
                .build();

        repository.save(preferencias);
    }

    @EventListener
    public void eventoColegaExcluido(ColegaExcluido evento) {

        PreferenciasColega preferencias = repository.findByColegaId(evento.colegaId())
                .orElseThrow(() ->
                        new PreferenciaColegaNaoEncontradaUsandoReferencia(evento.colegaId()));

        repository.deleteById(preferencias.getId());
    }
}