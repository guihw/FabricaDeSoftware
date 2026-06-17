package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

public record CreateColegaRequest(
        String nome,
        String email,
        String password,
        String cpf
) { }