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
import java.util.List;

@Service
public class CompatibilidadeService {

    @Autowired
    private IPreferenciasAnfitriao iPreferenciasAnfitriao;

    public int calcularScore(
            PreferenciasColegaResponse prefColega,
            CardAnfitriaoResponseDTO card,
            Long anfitriaoId
    ) {
        PreferenciasAnfitriaoDTO prefAnf =
                iPreferenciasAnfitriao.getPreferenciasAnfitriao(anfitriaoId);

        int score = 0;

        //Preço (30 pts)
        BigDecimal preco = card.precoMensal();
        if (preco != null && preco.compareTo(BigDecimal.ZERO) > 0) {
            if (prefColega.precoMinimo() != null && prefColega.precoMaximo() != null) {
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
        }

        //Localização (25 pts)
        if (prefColega.localizacao() != null && card.localizacao() != null) {
            String locColega = prefColega.localizacao().toLowerCase().trim();
            String locCard   = card.localizacao().toLowerCase().trim();
            if (locCard.contains(locColega) || locColega.contains(locCard)) {
                score += 25;
            } else if (primeirasPalavrasIguais(locColega, locCard)) {
                score += 12;
            }
        }

        //Animais (10 pts)
        // Usa presencaAnimais do anfitrião vs aceitaAnimais do colega.
        // O bônus de comodidade "pet" abaixo é complementar.
        if (prefAnf.presencaAnimais() == prefColega.aceitaAnimais()) {
            score += 10;
        }

        //Horário de sono vs horário de visitas (15 pts)
        if (prefColega.horarioDeSono() != null && prefAnf.horariosParaVisita() != null) {
            try {
                LocalTime fimVisitas = LocalTime.parse(prefAnf.horariosParaVisita());
                long diff = Duration.between(prefColega.horarioDeSono(), fimVisitas).toMinutes();
                if (diff <= 0)       score += 15;
                else if (diff <= 60) score += 8;
            } catch (Exception ignored) { /* horário mal-formado no banco */ }
        }

        // (até 20 pts)
        List<String> comodidades = card.comodidades();
        if (comodidades != null && !comodidades.isEmpty()) {
            score += calcularBonusComodidades(comodidades, prefColega);
        }

        return Math.min(score, 100);
    }
    //Calcula bônus baseado na compatibilidade entre comodidades do imóvel e as preferências do colega.
    //max de 20 pots
    private int calcularBonusComodidades(
            List<String> comodidades,
            PreferenciasColegaResponse prefColega
    ) {
        int bonus = 0;

        // Pet Friendly (+8): comodidade "pet" bate com colega que aceita animais
        if (comodidades.contains("pet") && prefColega.aceitaAnimais()) {
            bonus += 8;
        }

        // Coworking / Home Office (+6): bate com hábito de trabalho remoto ou híbrido
        boolean temEspacoTrabalho = comodidades.contains("coworking") || comodidades.contains("wifi");
        if (temEspacoTrabalho && prefColega.habitoDeTrabalho() != null) {
            String habito = prefColega.habitoDeTrabalho().name();
            if (habito.equals("HOME_OFFICE") || habito.equals("HIBRIDO")) {
                bonus += 6;
            }
        }

        // Silencioso (+6): bate com colega introvertido
        if (comodidades.contains("silencioso") && prefColega.nivelDeSociabilidade() != null) {
            if (prefColega.nivelDeSociabilidade().name().equals("INTROVERTIDO")) {
                bonus += 6;
            }
        }

        return Math.min(bonus, 20);
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