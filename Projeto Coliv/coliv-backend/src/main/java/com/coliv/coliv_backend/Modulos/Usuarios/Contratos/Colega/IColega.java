package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

public interface IColega {

    ColegaResponse getColega(Long id);

    public boolean verificarExistencia(Long id);
}