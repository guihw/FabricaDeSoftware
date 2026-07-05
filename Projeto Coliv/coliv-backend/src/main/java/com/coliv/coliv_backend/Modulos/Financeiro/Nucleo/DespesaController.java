package com.coliv.coliv_backend.Modulos.Financeiro.Nucleo;

import com.coliv.coliv_backend.Modulos.Financeiro.Contratos.DespesaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Despesas", description = "Gestão de despesas compartilhadas entre moradores")
@RestController
@RequestMapping("/despesas")
@CrossOrigin("*")
class DespesaController {

    @Autowired
    private DespesaService service;

    @Operation(summary = "Criar despesa")
    @PostMapping("/criar")
    public Despesa criar(@RequestBody DespesaDTO dto) {
        return service.criar(dto);
    }

    @Operation(summary = "Listar despesas de um anfitrião")
    @GetMapping("/anfitriao/{anfitriaoId}")
    public List<Despesa> listarPorAnfitriao(@PathVariable Long anfitriaoId) {
        return service.listarPorAnfitriao(anfitriaoId);
    }

    @Operation(summary = "Buscar despesa por ID")
    @GetMapping("/buscar/{id}")
    public Despesa buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Editar despesa")
    @PutMapping("/editar/{id}")
    public Despesa editar(@PathVariable Long id, @RequestBody DespesaDTO dto) {
        return service.editar(id, dto);
    }

    @Operation(summary = "Excluir despesa")
    @DeleteMapping("/excluir/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @Operation(summary = "Marcar despesa como paga", description = "Registra que o usuário pagou sua parte na despesa")
    @PatchMapping("/{id}/pagar/{usuarioId}")
    public Despesa marcarComoPago(@PathVariable Long id, @PathVariable Long usuarioId) {
        return service.marcarComoPago(id, usuarioId);
    }

    @Operation(summary = "Desmarcar pagamento de despesa")
    @PatchMapping("/{id}/desmarcar/{usuarioId}")
    public Despesa desmarcarComoPago(@PathVariable Long id, @PathVariable Long usuarioId) {
        return service.desmarcarComoPago(id, usuarioId);
    }
}
