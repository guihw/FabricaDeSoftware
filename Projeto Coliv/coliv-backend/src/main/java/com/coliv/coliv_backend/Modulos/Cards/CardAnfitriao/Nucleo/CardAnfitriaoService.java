package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class CardAnfitriaoService {

    @Autowired
    private CardAnfitriaoRepository car;

    public List<CardAnfitriao> listar() {
        return car.findAll();
    }

    public CardAnfitriao buscarPorId(Long id) {
        return car.findById(id).orElseThrow(() -> new CardAnfitriaoIDNaoEncontrado(id));
    }

    public CardAnfitriao criarCardAnfitriao(Long anfitriaoId, CardAnfitriaoDTO dto) {
        CardAnfitriao cardAnfitriao = new CardAnfitriao(dto.classificacao(), dto.precoMensal());
        cardAnfitriao.setAnfitriaoId(anfitriaoId);

        cardAnfitriao = car.save(cardAnfitriao);

        return cardAnfitriao;
    }

    public CardAnfitriao editarCardAnfitriao(Long anfitriaoId, CardAnfitriaoDTO dto) {
        CardAnfitriao cardAnfitriao = car.findByAnfitriaoId(anfitriaoId).orElseThrow(() -> new
                CardAnfitriaoNaoEncontradoUsandoReferencia(anfitriaoId));

        cardAnfitriao.setClassificacao(dto.classificacao());
        cardAnfitriao.setPrecoMensal(dto.precoMensal());

        return car.save(cardAnfitriao);
    }

    public void excluir(Long id) {
        car.findById(id).orElseThrow(() -> new CardAnfitriaoIDNaoEncontrado(id));
        car.deleteById(id);
    }

    @EventListener
    public void eventoAnfitriaoCriado(UsuarioAnfitriaoCriado evento) {
        CardAnfitriao cardAnfitriao = new CardAnfitriao();
        cardAnfitriao.setAnfitriaoId(evento.anfitriaoId());

        car.save(cardAnfitriao);
    }

    @EventListener
    public void eventoAnfitriaoExcluido(AnfitriaoExcluido evento) {
        CardAnfitriao cardAnfitriao = car.findByAnfitriaoId(evento.anfitriaoId()).orElseThrow(() -> new
                CardAnfitriaoNaoEncontradoUsandoReferencia(evento.anfitriaoId()));

        car.deleteById(cardAnfitriao.getId());
    }
}