package com.coliv.coliv_backend.Modulos.Recomendacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo.CardAnfitriaoService;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.IPreferenciasColega;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.FeedPageDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoCardAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoColegaDTO;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega.Colega;
import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega.ColegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class RecomendacaoService {

    @Autowired
    private IAnfitriao iAnfitriao;
    @Autowired private IPreferenciasColega iPreferenciasColega;
    @Autowired private CardAnfitriaoService cardAnfitriaoService;
    @Autowired private ColegaRepository colegaRepository;
    @Autowired private CompatibilidadeService compatibilidade;

    public FeedPageDTO<RecomendacaoCardAnfitriaoDTO> feedColega(
            Long colegaId, int pagina, int tamanhoPagina) {

        PreferenciasColegaResponse prefColega =
                iPreferenciasColega.getPreferenciasColega(colegaId);

        List<CardAnfitriaoResponseDTO> todosCards =
                cardAnfitriaoService.getCardCompleteInfoList();

        List<RecomendacaoCardAnfitriaoDTO> rankeados = todosCards.stream()
                .map(card -> {
                    int score = compatibilidade.calcularScore(
                            prefColega, card, card.anfitriaoId());
                    return new RecomendacaoCardAnfitriaoDTO(
                            card, score, compatibilidade.gerarResumo(score));
                })
                .filter(r -> r.score() > 0)
                .sorted(Comparator.comparingInt(
                        RecomendacaoCardAnfitriaoDTO::score).reversed())
                .toList();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioAutenticado usuario = (UsuarioAutenticado) authentication.getPrincipal();

        System.out.println("Tipo do usuário: [" + usuario.getTipo() + "]");
        System.out.println("Authorities: " + usuario.getAuthorities());

        return paginar(rankeados, pagina, tamanhoPagina);
    }

    public FeedPageDTO<RecomendacaoColegaDTO> feedAnfitriao(
            Long anfitriaoId, int pagina, int tamanhoPagina) {

        CardAnfitriaoResponseDTO cardAnfitriao =
                cardAnfitriaoService.getCardCompleteInfo(anfitriaoId);

        List<Colega> todosColeg = colegaRepository.findAll();

        List<RecomendacaoColegaDTO> rankeados = todosColeg.stream()
                .map(colega -> {
                    try {
                        PreferenciasColegaResponse prefColega =
                                iPreferenciasColega.getPreferenciasColega(colega.getId());

                        int score = compatibilidade.calcularScore(
                                prefColega, cardAnfitriao, anfitriaoId);

                        return new RecomendacaoColegaDTO(
                                colega.getId(),
                                colega.getNome(),
                                colega.getEmail(),
                                score,
                                compatibilidade.gerarResumo(score));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(r -> r.score() > 0)
                .sorted(Comparator.comparingInt(
                        RecomendacaoColegaDTO::score).reversed())
                .toList();

        return paginar(rankeados, pagina, tamanhoPagina);
    }

    private <T> FeedPageDTO<T> paginar(List<T> lista, int pagina, int tamanho) {
        int inicio = pagina * tamanho;
        if (inicio >= lista.size()) {
            return new FeedPageDTO<>(List.of(), pagina, tamanho,
                    lista.size(), false);
        }
        int fim = Math.min(inicio + tamanho, lista.size());
        return new FeedPageDTO<>(lista.subList(inicio, fim),
                pagina, tamanho, lista.size(), fim < lista.size());
    }
}
