package com.coliv.coliv_backend.Modulos.Recomendacao.Nucleo;

import com.coliv.coliv_backend.Modulos.Cards.CardAnfitriao.Contratos.CardAnfitriaoResponseDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.IPreferenciasAnfitriao;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos.PreferenciasAnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos.PreferenciasColegaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

@Service
public class CompatibilidadeService {

    @Autowired
    private IPreferenciasAnfitriao iPreferenciasAnfitriao;

    public int calcularScore(
            PreferenciasColegaResponse prefColega,
            CardAnfitriaoResponseDTO card,
            Long anfitriaoId) {

        PreferenciasAnfitriaoDTO prefAnf = iPreferenciasAnfitriao.getPreferenciasAnfitriao(anfitriaoId);

        int score = 0;

        // Preço (30 pontos)
        BigDecimal preco = card.precoMensal();
        if (preco != null) {
            if (preco.compareTo(prefColega.precoMinimo()) >= 0
                    && preco.compareTo(prefColega.precoMaximo()) <= 0) {
                score += 30;
            } else {
                BigDecimal margem = prefColega.precoMaximo()
                        .multiply(new BigDecimal("0.20"));
                if (preco.compareTo(prefColega.precoMaximo().add(margem)) <= 0) {
                    score += 15;
                }
            }
        }

        // Localização (25 pontos)
        if (prefColega.localizacao() != null && card.localizacao() != null) {
            String locColega = prefColega.localizacao().toLowerCase().trim();
            String locCard   = card.localizacao().toLowerCase().trim();
            if (locCard.contains(locColega) || locColega.contains(locCard)) {
                score += 25;
            } else if (primeirasPalavrasIguais(locColega, locCard)) {
                score += 12;
            }
        }

        // Animais (10 pontos)
        if (prefAnf.presencaAnimais() == prefColega.aceitaAnimais()) {
            score += 10;
        }

        // Horário (15 pontos)
        if (prefColega.horarioDeSono() != null
                && prefAnf.horariosParaVisita() != null) {
            LocalTime fimVisitas = LocalTime.parse(prefAnf.horariosParaVisita());
            long diff = Duration.between(prefColega.horarioDeSono(), fimVisitas)
                    .toMinutes();
            if (diff <= 0)       score += 15;
            else if (diff <= 60) score += 8;
        }

        return Math.min(score, 100);
    }

    public String gerarResumo(int score) {
        if (score >= 80) return "Excelente compatibilidade";
        if (score >= 60) return "Boa compatibilidade";
        if (score >= 40) return "Compatibilidade moderada";
        return "Baixa compatibilidade";
    }

    private boolean primeirasPalavrasIguais(String a, String b) {
        String[] pa = a.split("\\s+");
        String[] pb = b.split("\\s+");
        return pa.length > 0 && pb.length > 0 && pa[0].equalsIgnoreCase(pb[0]);
    }
}