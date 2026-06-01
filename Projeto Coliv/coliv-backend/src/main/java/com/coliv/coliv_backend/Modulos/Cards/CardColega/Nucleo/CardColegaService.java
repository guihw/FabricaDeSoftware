package com.coliv.coliv_backend.Modulos.Cards.CardColega.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardColega.Contratos.*;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.IPreferenciasColega;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.ColegaExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.UsuarioColegaCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.ColegaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
class CardColegaService {

    @Autowired
    private CardColegaRepository repository;

    @Autowired
    private IColega iColega;

    @Autowired
    private IPreferenciasColega iPreferenciasColega;

    public List<CardColega> listar() {
        return repository.findAll();
    }

    public CardColega buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new CardColegaIDNaoEncontrado(id));
    }

    public CardColegaResponseDTO getCardCompleteInfo(Long colegaId) {

        ColegaResponse colegaDTO =
                iColega.getColega(colegaId);

        PreferenciasColegaResponse preferenciasDTO =
                iPreferenciasColega.getPreferenciasColega(colegaId);

        CardColega card =
                repository.findByColegaId(colegaId)
                        .orElseThrow(() ->
                                new CardColegaNaoEncontradoUsandoReferencia(colegaId));

        return new CardColegaResponseDTO(
                colegaDTO.nome(),
                colegaDTO.email(),
                colegaDTO.descricao(),
                card.getClassificacao(),

                preferenciasDTO.localizacao(),

                preferenciasDTO.precoMinimo(),
                preferenciasDTO.precoMaximo(),

                preferenciasDTO.horarioDeSono(),

                preferenciasDTO.nivelDeSociabilidade(),
                preferenciasDTO.nivelDeLimpeza(),
                preferenciasDTO.habitoDeTrabalho(),

                preferenciasDTO.aceitaAnimais()
        );
    }

   /* public List<CardColegaResponseDTO> getCardCompleteInfoList() {

        List<UsuarioDTO> usuarios =
                iColega.obterListaDeUsuarios();

        List<CardColega> cards =
                repository.findAll();

        Map<Long, UsuarioDTO> usuarioMap =
                usuarios.stream().collect(
                        Collectors.toMap(
                                UsuarioDTO::id,
                                usuario -> usuario
                        )
                );

        return cards.stream().map(card -> {

            UsuarioDTO usuarioDTO =
                    usuarioMap.get(card.getColegaId());

            PreferenciasColegaDTO preferenciasDTO =
                    iPreferenciasColega.getPreferenciasColega(
                            card.getColegaId()
                    );

            return new CardColegaResponseDTO(
                    usuarioDTO.nome(),
                    usuarioDTO.email(),
                    usuarioDTO.descricao(),
                    card.getClassificacao(),

                    preferenciasDTO.localizacao(),

                    preferenciasDTO.precoMinimo(),
                    preferenciasDTO.precoMaximo(),

                    preferenciasDTO.horarioDeSono(),

                    preferenciasDTO.nivelDeSociabilidade(),
                    preferenciasDTO.nivelDeLimpeza(),
                    preferenciasDTO.habitoDeTrabalho(),

                    preferenciasDTO.aceitaAnimais()
            );

        }).toList();
    }*/

    public void excluir(Long id) {

        repository.findById(id)
                .orElseThrow(() ->
                        new CardColegaIDNaoEncontrado(id));

        repository.deleteById(id);
    }

    @EventListener
    public void eventoColegaCriado(
            UsuarioColegaCriado evento
    ) {

        CardColega card = new CardColega();

        card.setColegaId(evento.colegaId());

        repository.save(card);
    }

    @EventListener
    public void eventoColegaExcluido(
            ColegaExcluido evento
    ) {

        CardColega card =
                repository.findByColegaId(evento.colegaId())
                        .orElseThrow(() ->
                                new CardColegaNaoEncontradoUsandoReferencia(
                                        evento.colegaId()
                                ));

        repository.deleteById(card.getId());
    }
}