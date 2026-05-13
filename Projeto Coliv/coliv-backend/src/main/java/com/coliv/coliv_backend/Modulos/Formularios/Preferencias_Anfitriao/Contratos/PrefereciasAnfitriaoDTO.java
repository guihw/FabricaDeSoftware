package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos;

import java.time.LocalTime;

public record PrefereciasAnfitriaoDTO(boolean presencaAnimais, String horariosParaVisita, String politicaDeLimpeza,
                                      String regrasDaCasa, String perfilColegaDesejado) {
}
