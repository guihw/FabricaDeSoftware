package com.coliv.coliv_backend.Modulos.Formularios.Preferencias_Anfitriao.Contratos;

public class PreferenciaNaoEncontradaUsandoReferencia extends RuntimeException {
    public PreferenciaNaoEncontradaUsandoReferencia(Long id) {
        super("Nenhuma Preferencia encontrada utilizando o id " + id + " como Referencia");
    }
}
