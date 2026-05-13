package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos;

public class PreferenciaAnfitriaoIDNaoEncontrado extends RuntimeException {
    public PreferenciaAnfitriaoIDNaoEncontrado(Long id) {
        super("Preferência de Id " + id + " não encontrada . . .");
    }
}
