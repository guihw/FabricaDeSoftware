package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

import java.util.Optional;

public interface IColega {

    ColegaResponse getColega(Long id);

    boolean verificarExistencia(Long id);

    Optional<ColegaCredenciaisDTO> buscarCredenciais(String email);
}