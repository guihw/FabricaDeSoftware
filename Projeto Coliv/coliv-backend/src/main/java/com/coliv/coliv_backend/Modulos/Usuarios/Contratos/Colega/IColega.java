package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

import java.util.List;
import java.util.Optional;

public interface IColega {

    ColegaResponse getColega(Long id);

    List<ColegaResponse> getColegas(List<Long> ids);

    boolean verificarExistencia(Long id);

    Optional<ColegaCredenciaisDTO> buscarCredenciais(String email);
    void ativarPlano(Long id);
}