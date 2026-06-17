package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.ColegaCredenciaisDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColegaRepository extends JpaRepository<Colega,Long> {
    boolean existsByEmail(String email);
    Optional<Colega> findByEmail(String email);
}
