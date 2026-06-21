package com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.MembroInfoDTO;

import java.util.List;

public record GrupoResponseDTO(Long grupoId, List<MembroInfoDTO> membroList, String nomeGrupo) {
}
