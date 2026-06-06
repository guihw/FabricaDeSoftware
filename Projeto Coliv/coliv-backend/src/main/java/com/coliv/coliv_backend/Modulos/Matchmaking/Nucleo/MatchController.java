package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
@CrossOrigin("*")
class MatchController {

    @Autowired
    private MatchService service;

    @PostMapping("/{colegaId}/{anfitriaoId}")
    public MatchResponse criar(
            @PathVariable Long colegaId,
            @PathVariable Long anfitriaoId
    ) {

        return service.criar(
                colegaId,
                anfitriaoId
        );
    }

    @GetMapping("/{id}")
    public MatchResponse buscar(
            @PathVariable Long id
    ) {

        return service.buscar(id);
    }

    @DeleteMapping("/{id}")
    public void cancelar(
            @PathVariable Long id
    ) {

        service.cancelar(id);
    }
}