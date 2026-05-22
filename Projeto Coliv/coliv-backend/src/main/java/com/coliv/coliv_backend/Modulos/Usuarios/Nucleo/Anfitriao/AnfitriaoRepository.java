package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface AnfitriaoRepository extends JpaRepository<Anfitriao, Long> {

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
