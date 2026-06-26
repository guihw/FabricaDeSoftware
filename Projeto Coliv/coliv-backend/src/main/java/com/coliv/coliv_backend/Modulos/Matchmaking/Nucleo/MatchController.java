package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchDTO;
import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Matches", description = "Criação e gerenciamento de matches entre colega e anfitrião. Endpoints públicos (não requerem token).")
@RestController
@RequestMapping("/matches")
@CrossOrigin("*")
class MatchController {

    @Autowired
    private MatchService service;

    @Operation(summary = "Criar match", description = "Colega demonstra interesse em um imóvel; cria o match com status PENDENTE")
    @SecurityRequirements
    @PostMapping("/novo")
    public MatchResponse criar(@RequestBody MatchDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Criar match já aceito", description = "Anfitrião aceita diretamente um colega do feed; match nasce com status ACEITO")
    @SecurityRequirements
    @PostMapping("/{colegaId}/{anfitriaoId}/aceitar")
    public MatchResponse criarAceito(@PathVariable Long colegaId, @PathVariable Long anfitriaoId) {
        return service.criarAceito(colegaId, anfitriaoId);
    }

    @Operation(summary = "Buscar match por ID")
    @SecurityRequirements
    @GetMapping("/{id}")
    public MatchResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Aceitar match pendente", description = "Atualiza o status do match para ACEITO")
    @SecurityRequirements
    @PatchMapping("/aceitar/{id}")
    public void aceitar(@PathVariable Long id) {
        service.aceitar(id);
    }

    @Operation(summary = "Cancelar match")
    @SecurityRequirements
    @DeleteMapping("/{id}")
    public void cancelar(@PathVariable Long id) {
        service.cancelar(id);
    }
}
