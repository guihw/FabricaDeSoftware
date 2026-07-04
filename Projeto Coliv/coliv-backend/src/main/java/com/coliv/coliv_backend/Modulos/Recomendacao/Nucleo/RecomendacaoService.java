package com.coliv.coliv_backend.Modulos.Recomendacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Nucleo.ArquivoRepository;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Nucleo.CardAnfitriaoService;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.IPreferenciasColega;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.IMatchmaking;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo.StatusMatch;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.FeedPageDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoCardAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Recomendacao.Contratos.RecomendacaoColegaDTO;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega.Colega;
import com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega.ColegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RecomendacaoService {

    @Autowired
    private IAnfitriao iAnfitriao;
    @Autowired private IPreferenciasColega iPreferenciasColega;
    @Autowired private CardAnfitriaoService cardAnfitriaoService;
    @Autowired private ColegaRepository colegaRepository;
    @Autowired private CompatibilidadeService compatibilidade;
    @Autowired private ArquivoRepository arquivoRepository;
    @Autowired private IMatchmaking matchmaking;

    public FeedPageDTO<RecomendacaoCardAnfitriaoDTO> feedColega(
            Long colegaId, int pagina, int tamanhoPagina) {

        PreferenciasColegaResponse prefColega =
                iPreferenciasColega.getPreferenciasColega(colegaId);

        List<CardAnfitriaoResponseDTO> todosCards =
                cardAnfitriaoService.getCardCompleteInfoList();

        Set<Long> anfitriaoIdsComInteresse = anfitriaoIdsComMatchAtivo(colegaId);

        List<RecomendacaoCardAnfitriaoDTO> rankeados = todosCards.stream()
                .filter(card -> !anfitriaoIdsComInteresse.contains(card.anfitriaoId()))
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

        Set<Long> colegaIdsComInteresse = colegaIdsComMatchAtivo(anfitriaoId);

        List<RecomendacaoColegaDTO> rankeados = todosColeg.stream()
                .filter(colega -> !colegaIdsComInteresse.contains(colega.getId()))
                .map(colega -> {
                    try {
                        PreferenciasColegaResponse prefColega =
                                iPreferenciasColega.getPreferenciasColega(colega.getId());

                        int score = compatibilidade.calcularScore(
                                prefColega, cardAnfitriao, anfitriaoId);

                        String fotoPerfil = null;
                        if (colega.getFotoPerfilId() != null) {
                            fotoPerfil = arquivoRepository.findById(colega.getFotoPerfilId())
                                    .map(a -> a.getUrl())
                                    .orElse(null);
                        }

                        return new RecomendacaoColegaDTO(
                                colega.getId(),
                                colega.getNome(),
                                colega.getEmail(),
                                score,
                                compatibilidade.gerarResumo(score),
                                fotoPerfil);
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

    // Anfitriões que devem sumir do feed do colega:
    // - match já ACEITO (chat já existe, não faz sentido continuar aparecendo);
    // - PENDENTE com iniciador COLEGA (o próprio colega já demonstrou interesse — evita clique duplicado).
    // Um PENDENTE com iniciador ANFITRIAO é mantido visível de propósito: é o card que,
    // se o colega curtir, completa o match (fluxo de "match mútuo").
    private Set<Long> anfitriaoIdsComMatchAtivo(Long colegaId) {
        return matchmaking.listarPorColega(colegaId).stream()
                .filter(m -> m.status() == StatusMatch.ACEITO
                        || (m.status() == StatusMatch.PENDENTE && m.iniciador() == TipoUsuario.COLEGA))
                .map(MatchResponse::anfitriaoId)
                .collect(java.util.stream.Collectors.toSet());
    }

    // Simétrico ao método acima, do ponto de vista do anfitrião.
    private Set<Long> colegaIdsComMatchAtivo(Long anfitriaoId) {
        return matchmaking.listarPorAnfitriao(anfitriaoId).stream()
                .filter(m -> m.status() == StatusMatch.ACEITO
                        || (m.status() == StatusMatch.PENDENTE && m.iniciador() == TipoUsuario.ANFITRIAO))
                .map(MatchResponse::colegaId)
                .collect(java.util.stream.Collectors.toSet());
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
