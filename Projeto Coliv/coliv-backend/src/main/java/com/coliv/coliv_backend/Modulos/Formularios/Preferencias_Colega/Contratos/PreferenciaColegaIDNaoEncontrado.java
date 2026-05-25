package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos;

public class PreferenciaColegaIDNaoEncontrado extends RuntimeException {

    public PreferenciaColegaIDNaoEncontrado(Long id) {
        super("Preferência de colega com id " + id + " não encontrada.");
    }
}