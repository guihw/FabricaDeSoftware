package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelDTO;
import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.DadosImovelRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dados do Imóvel", description = "Formulário com informações do imóvel do anfitrião. Requer role ANFITRIÃO.")
@RestController
@RequestMapping("/formularios/dados-imovel")
@CrossOrigin("*")
class DadosImovelController {

    @Autowired
    private DadosImovelService dis;

    @Operation(summary = "Listar dados de imóveis")
    @GetMapping("/listar")
    public List<DadosImovel> listar() {
        return dis.listar();
    }

    @Operation(summary = "Buscar dados de imóvel por ID")
    @GetMapping("/buscar/{id}")
    public DadosImovel buscarPorId(@PathVariable Long id) {
        return dis.buscarPorId(id);
    }

    @Operation(summary = "Buscar dados do imóvel do anfitrião", description = "Retorna 204 se o anúncio ainda não estiver completo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados encontrados"),
        @ApiResponse(responseCode = "204", description = "Anúncio incompleto ou não criado")
    })
    @GetMapping("/anfitriao/{anfitriaoId}")
    public ResponseEntity<DadosImovelDTO> buscarPorAnfitriaoId(@PathVariable Long anfitriaoId) {
        return dis.getDadosImovelSeCompleto(anfitriaoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Criar dados do imóvel")
    @PostMapping("/{anfitriaoId}/novo-dados-imovel")
    public DadosImovelRequestDTO criarDadosImovel(@PathVariable Long anfitriaoId,
                                                   @RequestBody DadosImovelRequestDTO dto) {
        return dis.criarDadosImovel(anfitriaoId, dto);
    }

    @Operation(summary = "Editar dados do imóvel", description = "Restrito ao próprio anfitrião autenticado")
    @PreAuthorize("#anfitriaoId == authentication.principal.id")
    @PutMapping("/editar/{anfitriaoId}")
    public DadosImovelRequestDTO editarDadosImovel(@PathVariable Long anfitriaoId,
                                                    @RequestBody DadosImovelRequestDTO dto) {
        return dis.editarDadosImovel(anfitriaoId, dto);
    }

    @Operation(summary = "Excluir dados do imóvel")
    @DeleteMapping("excluir/{id}")
    public void excluir(@PathVariable Long id) {
        dis.excluir(id);
    }
}
