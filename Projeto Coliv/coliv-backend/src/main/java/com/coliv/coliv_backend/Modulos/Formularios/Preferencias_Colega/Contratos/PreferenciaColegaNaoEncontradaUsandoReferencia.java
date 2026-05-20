package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Colega.Contratos;

public class PreferenciaColegaNaoEncontradaUsandoReferencia extends RuntimeException {

    public PreferenciaColegaNaoEncontradaUsandoReferencia(Long colegaId) {
        super("Nenhuma preferência encontrada utilizando o colegaId "
                + colegaId + " como referência.");
    }
}