package com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.IArquivos;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoRequestDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.IDadosImovel;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CardAnfitriaoService {

    @Autowired private CardAnfitriaoRepository car;
    @Autowired private IAnfitriao iAnfitriao;
    @Autowired private IDadosImovel iDadosImovel;
    @Autowired private IArquivos iArquivos;

    public List<CardAnfitriao> listar() {
        return car.findAll();
    }

    public CardAnfitriao buscarPorId(Long id) {
        return car.findById(id).orElseThrow(() -> new CardAnfitriaoIDNaoEncontrado(id));
    }

    public CardAnfitriaoResponseDTO getCardCompleteInfo(Long anfitriaoId) {
        UsuarioDTO    usuarioDTO = iAnfitriao.obterUsuario(anfitriaoId);
        DadosImovelDTO imovelDTO = iDadosImovel.getDadosImovel(anfitriaoId);
        CardAnfitriao  card      = car.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new CardAnfitriaoNaoEncontradoUsandoReferencia(anfitriaoId));

        return toResponseDTO(card, usuarioDTO, imovelDTO);
    }


    public List<CardAnfitriaoResponseDTO> getCardCompleteInfoList() {
        List<UsuarioDTO>    usuarios = iAnfitriao.obterListaDeUsuarios();
        List<DadosImovelDTO> imoveis = iDadosImovel.obterListaDeDados();
        List<CardAnfitriao>  cards   = car.findAll();

        Map<Long, UsuarioDTO>    usuarioMap = usuarios.stream()
                .collect(Collectors.toMap(UsuarioDTO::id, u -> u, (a, b) -> a));
        Map<Long, DadosImovelDTO> imovelMap = imoveis.stream()
                .filter(d -> d.anfitriaoId() != null)
                .collect(Collectors.toMap(DadosImovelDTO::anfitriaoId, d -> d, (a, b) -> a));

        return cards.stream()
                .filter(c -> c.getAnfitriaoId() != null
                        && usuarioMap.containsKey(c.getAnfitriaoId())
                        && imovelMap.containsKey(c.getAnfitriaoId()))
                .collect(Collectors.toMap(
                        CardAnfitriao::getAnfitriaoId,
                        c -> c,
                        (a, b) -> a
                ))
                .values()
                .stream()
                .map(card -> toResponseDTO(
                        card,
                        usuarioMap.get(card.getAnfitriaoId()),
                        imovelMap.get(card.getAnfitriaoId())
                ))
                .toList();
    }

    //CRUD do card
    public CardAnfitriaoRequestDTO criarCardAnfitriao(Long anfitriaoId, CardAnfitriaoRequestDTO dto) {
        CardAnfitriao cardAnfitriao = car.findByAnfitriaoId(anfitriaoId)
                .orElseGet(CardAnfitriao::new);

        cardAnfitriao.setAnfitriaoId(anfitriaoId);
        car.save(cardAnfitriao);
        return dto;
    }

    public CardAnfitriaoRequestDTO editarCardAnfitriao(Long anfitriaoId, CardAnfitriaoRequestDTO dto) {
        CardAnfitriao cardAnfitriao = car.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new CardAnfitriaoNaoEncontradoUsandoReferencia(anfitriaoId));
        car.save(cardAnfitriao);
        return dto;
    }

    public void atualizarArquivos(Long anfitriaoId, List<Long> arquivos) {
        CardAnfitriao card = car.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new CardAnfitriaoNaoEncontradoUsandoReferencia(anfitriaoId));
        card.setArquivos(arquivos);
        car.save(card);
    }

    public void excluir(Long id) {
        car.findById(id).orElseThrow(() -> new CardAnfitriaoIDNaoEncontrado(id));
        car.deleteById(id);
    }

    @EventListener
    public void eventoAnfitriaoCriado(UsuarioAnfitriaoCriado evento) {
        if (car.findByAnfitriaoId(evento.anfitriaoId()).isEmpty()) {
            CardAnfitriao cardAnfitriao = new CardAnfitriao();
            cardAnfitriao.setAnfitriaoId(evento.anfitriaoId());
            car.save(cardAnfitriao);
        }
    }

    @EventListener
    public void eventoAnfitriaoExcluido(AnfitriaoExcluido evento) {
        CardAnfitriao cardAnfitriao = car.findByAnfitriaoId(evento.anfitriaoId())
                .orElseThrow(() -> new CardAnfitriaoNaoEncontradoUsandoReferencia(evento.anfitriaoId()));
        car.deleteById(cardAnfitriao.getId());
    }

    private CardAnfitriaoResponseDTO toResponseDTO(
            CardAnfitriao card,
            UsuarioDTO usuario,
            DadosImovelDTO imovel
    ) {
        List<Long> ids = card.getArquivos() != null ? card.getArquivos() : new ArrayList<>();
        List<String> urls = ids.isEmpty()
                ? new ArrayList<>()
                : iArquivos.getArquivos(ids).stream().map(a -> a.url()).toList();

        return new CardAnfitriaoResponseDTO(
                card.getAnfitriaoId(),
                usuario.nome(),
                usuario.email(),
                imovel.descricao(),
                imovel.localizacao(),
                imovel.quartos(),
                card.getClassificacao(),
                imovel.precoMensal() != null ? imovel.precoMensal() : BigDecimal.ZERO,
                urls,
                imovel.tipoVaga(),
                imovel.comodidades() != null ? imovel.comodidades() : new ArrayList<>()
        );
    }
}