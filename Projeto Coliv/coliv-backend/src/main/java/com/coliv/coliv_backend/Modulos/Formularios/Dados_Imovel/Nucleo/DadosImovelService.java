package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
class DadosImovelService implements IDadosImovel {

    @Autowired
    private DadosImovelRepository dir;

    public List<DadosImovel> listar() {
        return dir.findAll();
    }

    public DadosImovel buscarPorId(Long id) {
        return dir.findById(id).orElseThrow(() -> new DadosImovelIDNaoEncontrado(id));
    }

    // Criar / Upsert

    @Transactional
    public DadosImovelRequestDTO criarDadosImovel(Long anfitriaoId, DadosImovelRequestDTO dto) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId)
                .orElseGet(DadosImovel::new);

        dadosImovel.setAnfitriaoId(anfitriaoId);
        dadosImovel.setDescricao(dto.descricao());
        dadosImovel.setLocalizacao(dto.localizacao());
        dadosImovel.setQuartos(dto.quartos());
        dadosImovel.setPrecoMensal(dto.precoMensal());
        dadosImovel.setTipoVaga(dto.tipoVaga());

        List<String> novasComodidades = dto.comodidades() != null ? dto.comodidades() : new ArrayList<>();
        dadosImovel.getComodidades().clear();
        dadosImovel.getComodidades().addAll(novasComodidades);

        dir.save(dadosImovel);
        return dto;
    }

    @Transactional
    public DadosImovelRequestDTO editarDadosImovel(Long anfitriaoId, DadosImovelRequestDTO dto) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new DadosImovelNaoEncontradoUsandoReferencia(anfitriaoId));

        if (dto.descricao() != null && !dto.descricao().isBlank())
            dadosImovel.setDescricao(dto.descricao());
        if (dto.localizacao() != null && !dto.localizacao().isBlank())
            dadosImovel.setLocalizacao(dto.localizacao());

        dadosImovel.setQuartos(dto.quartos());

        if (dto.precoMensal() != null)
            dadosImovel.setPrecoMensal(dto.precoMensal());
        if (dto.tipoVaga() != null && !dto.tipoVaga().isBlank())
            dadosImovel.setTipoVaga(dto.tipoVaga());
        if (dto.comodidades() != null) {
            dadosImovel.getComodidades().clear();
            dadosImovel.getComodidades().addAll(dto.comodidades());
        }

        dir.save(dadosImovel);
        return dto;
    }

    public void excluir(Long id) {
        dir.findById(id).orElseThrow(() -> new DadosImovelIDNaoEncontrado(id));
        dir.deleteById(id);
    }


    @Override
    public DadosImovelDTO getDadosImovel(Long anfitriaoId) {
        DadosImovel d = dir.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new DadosImovelNaoEncontradoUsandoReferencia(anfitriaoId));

        return toDTO(d);
    }

    public Optional<DadosImovelDTO> getDadosImovelSeCompleto(Long anfitriaoId) {
        return dir.findByAnfitriaoId(anfitriaoId)
                .filter(d -> d.getDescricao() != null && !d.getDescricao().isBlank()
                        && d.getLocalizacao() != null && !d.getLocalizacao().isBlank())
                .map(this::toDTO);
    }

    @Override
    public List<DadosImovelDTO> obterListaDeDados() {
        return dir.findAll().stream().map(this::toDTO).toList();
    }


    @EventListener
    public void eventoAnfitriaoCriado(UsuarioAnfitriaoCriado evento) {
        if (dir.findByAnfitriaoId(evento.anfitriaoId()).isEmpty()) {
            DadosImovel dadosImovel = new DadosImovel();
            dadosImovel.setAnfitriaoId(evento.anfitriaoId());
            dir.save(dadosImovel);
        }
    }

    @EventListener
    public void eventoAnfitriaoExcluido(AnfitriaoExcluido evento) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(evento.anfitriaoId())
                .orElseThrow(() -> new DadosImovelNaoEncontradoUsandoReferencia(evento.anfitriaoId()));
        dir.deleteById(dadosImovel.getId());
    }

    // ── Helper ────────────────────────────────────────────────────

    private DadosImovelDTO toDTO(DadosImovel d) {
        return new DadosImovelDTO(
                d.getAnfitriaoId(),
                d.getDescricao(),
                d.getLocalizacao(),
                d.getQuartos(),
                d.getPrecoMensal(),
                d.getTipoVaga(),
                d.getComodidades()
        );
    }
}