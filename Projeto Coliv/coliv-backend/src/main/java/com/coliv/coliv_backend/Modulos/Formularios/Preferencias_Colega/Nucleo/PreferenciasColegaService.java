package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciaColegaIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciaColegaNaoEncontradaUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferenciasColegaService {

    @Autowired
    private PreferenciasColegaRepository repository;

    public List<PreferenciasColega> listar() {
        return repository.findAll();
    }

    public PreferenciasColega buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));
    }

    public PreferenciasColega criarPreferencia(Long colegaId, PreferenciasColegaDTO dto) {

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

        return repository.save(preferencias);
    }

    public PreferenciasColega editarPreferencias(Long id, PreferenciasColegaDTO dto) {

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

        return repository.save(original);
    }

    public void excluir(Long id) {

        repository.findById(id)
                .orElseThrow(() -> new PreferenciaColegaIDNaoEncontrado(id));

        repository.deleteById(id);
    }

    public PreferenciasColegaDTO getPreferenciasColega(Long colegaId) {

        PreferenciasColega preferencias = repository.findByColegaId(colegaId)
                .orElseThrow(() ->
                        new PreferenciaColegaNaoEncontradaUsandoReferencia(colegaId));

        return new PreferenciasColegaDTO(
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
}