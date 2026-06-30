package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega;

public record UpdateColegaRequest(

        String nome,
        String cpf,
        String email,
        String password,
        String descricao,
        Long fotoPerfilId

) {
}