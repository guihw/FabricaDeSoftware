package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

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

    public DadosImovelRequestDTO criarDadosImovel(Long anfitriaoId, DadosImovelRequestDTO dto) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId)
                .orElseGet(DadosImovel::new);

        dadosImovel.setAnfitriaoId(anfitriaoId);
        dadosImovel.setDescricao(dto.descricao());
        dadosImovel.setLocalizacao(dto.localizacao());
        dadosImovel.setQuartos(dto.quartos());

        dir.save(dadosImovel);
        return dto;
    }

    public DadosImovelRequestDTO editarDadosImovel(Long anfitriaoId, DadosImovelRequestDTO dto) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new DadosImovelNaoEncontradoUsandoReferencia(anfitriaoId));

        if (dto.descricao() != null && !dto.descricao().isBlank()) {
            dadosImovel.setDescricao(dto.descricao());
        }
        if (dto.localizacao() != null && !dto.localizacao().isBlank()) {
            dadosImovel.setLocalizacao(dto.localizacao());
        }
        dadosImovel.setQuartos(dto.quartos());

        dir.save(dadosImovel);
        return dto;
    }

    public void excluir(Long id) {
        dir.findById(id).orElseThrow(() -> new DadosImovelIDNaoEncontrado(id));
        dir.deleteById(id);
    }

    @Override
    public DadosImovelDTO getDadosImovel(Long anfitriaoId) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId)
                .orElseThrow(() -> new DadosImovelNaoEncontradoUsandoReferencia(anfitriaoId));

        return new DadosImovelDTO(
                dadosImovel.getAnfitriaoId(),
                dadosImovel.getDescricao(),
                dadosImovel.getLocalizacao(),
                dadosImovel.getQuartos()
        );
    }
    public Optional<DadosImovelDTO> getDadosImovelSeCompleto(Long anfitriaoId) {
        return dir.findByAnfitriaoId(anfitriaoId)
                .filter(d -> d.getDescricao() != null && !d.getDescricao().isBlank()
                        && d.getLocalizacao() != null && !d.getLocalizacao().isBlank())
                .map(d -> new DadosImovelDTO(
                        d.getAnfitriaoId(),
                        d.getDescricao(),
                        d.getLocalizacao(),
                        d.getQuartos()
                ));
    }

    @Override
    public List<DadosImovelDTO> obterListaDeDados() {
        return dir.findAll().stream()
                .map(d -> new DadosImovelDTO(d.getAnfitriaoId(), d.getDescricao(),
                        d.getLocalizacao(), d.getQuartos()))
                .toList();
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
}