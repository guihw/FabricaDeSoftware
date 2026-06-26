package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoDTO;
import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.ArquivoRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Arquivos", description = "Upload e gerenciamento de imagens no Cloudflare R2. Limite: 10 arquivos por requisição, 5 MB cada, apenas JPEG e PNG.")
@RestController
@RequestMapping("/arquivos")
class ArquivoController {

    @Autowired
    private ArquivoService service;

    @Operation(summary = "Listar todos os arquivos")
    @GetMapping
    public List<Arquivo> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar arquivo por ID", description = "Retorna metadados do arquivo incluindo URL pública")
    @GetMapping("/{id}")
    public ArquivoDTO buscar(@PathVariable Long id) {
        return service.getArquivo(id);
    }

    @Operation(summary = "Criar registro de arquivo manualmente")
    @PostMapping
    public ArquivoDTO criar(@RequestBody ArquivoRequestDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Fazer upload de imagens", description = "Envia até 10 imagens (JPEG/PNG, máx 5 MB cada) para o Cloudflare R2 e retorna as URLs públicas com os IDs gerados")
    @PostMapping("/upload")
    public List<ArquivoDTO> upload(@RequestParam("arquivos") MultipartFile[] arquivos) {
        return service.upload(arquivos);
    }

    @Operation(summary = "Editar metadados de arquivo")
    @PutMapping("/{id}")
    public ArquivoDTO editar(@PathVariable Long id, @RequestBody ArquivoRequestDTO dto) {
        return service.editar(id, dto);
    }

    @Operation(summary = "Excluir arquivo")
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
