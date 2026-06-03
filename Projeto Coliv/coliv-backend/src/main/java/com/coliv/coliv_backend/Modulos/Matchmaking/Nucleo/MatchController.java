package com.coliv.coliv_backend.Modulos.Matchmaking.Nucleo;

import com.coliv.coliv_backend.Modulos.Matchmaking.Contratos.MatchResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
@CrossOrigin("*")
class MatchController {

    private final MatchService service;

    MatchController(MatchService service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public List<MatchResponse> listar() {
        return service.listar();
    }

    @GetMapping("/buscar/{id}")
    public MatchResponse buscar(
            @PathVariable Long id
    ) {
        return service.buscar(id);
    }
}