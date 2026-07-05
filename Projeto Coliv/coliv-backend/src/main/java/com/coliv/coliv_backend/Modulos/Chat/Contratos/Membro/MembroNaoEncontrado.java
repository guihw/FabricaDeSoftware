package com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro;

public class MembroNaoEncontrado extends RuntimeException {
    public MembroNaoEncontrado(Long grupoId, Long usuarioId) {
        super("Nenhum Membro com usuarioId " + usuarioId + " encontrado no Grupo " + grupoId);
    }
}
