package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoDTO;
import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arquivos")
class ArquivoController {

    @Autowired
    private ArquivoService service;

    @GetMapping
    public List<Arquivo> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ArquivoDTO buscar(@PathVariable Long id) {
        return service.getArquivo(id);
    }

    @PostMapping
    public ArquivoDTO criar(
            @RequestBody ArquivoRequestDTO dto
    ) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    public ArquivoDTO editar(
            @PathVariable Long id,
            @RequestBody ArquivoRequestDTO dto
    ) {
        return service.editar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);
    }
}