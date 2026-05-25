package com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Nucleo;

import com.coliv.coliv_backend.Modulos.Formularios.Dados_Imovel.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

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
        DadosImovel dadosImovel = new DadosImovel(dto.descricao(), dto.localizacao(), dto.quartos());
        dadosImovel.setAnfitriaoId(anfitriaoId);

        dir.save(dadosImovel);

        return dto;
    }


    public DadosImovelRequestDTO editarDadosImovel(Long anfitriaoId, DadosImovelRequestDTO dto) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId).orElseThrow(() -> new
                DadosImovelNaoEncontradoUsandoReferencia(anfitriaoId));

        if(dto.descricao() != null && !dto.descricao().isBlank()) {
            dadosImovel.setDescricao(dto.descricao());
        }
        if(dto.localizacao() != null && !dto.localizacao().isBlank()) {
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

    @EventListener
    public void eventoAnfitriaoCriado(UsuarioAnfitriaoCriado evento) {
        DadosImovel dadosImovel = new DadosImovel();
        dadosImovel.setAnfitriaoId(evento.anfitriaoId());

        dir.save(dadosImovel);
    }

    @EventListener
    public void eventoAnfitriaoExcluido(AnfitriaoExcluido evento) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(evento.anfitriaoId()).orElseThrow(() -> new
                DadosImovelNaoEncontradoUsandoReferencia(evento.anfitriaoId()));

        dir.deleteById(dadosImovel.getId());
    }

    @Override
    public DadosImovelDTO getDadosImovel(Long anfitriaoId) {
        DadosImovel dadosImovel = dir.findByAnfitriaoId(anfitriaoId).orElseThrow(() -> new
                DadosImovelNaoEncontradoUsandoReferencia(anfitriaoId));

        return new DadosImovelDTO(dadosImovel.getAnfitriaoId(), dadosImovel.getDescricao(),
                dadosImovel.getLocalizacao(), dadosImovel.getQuartos());
    }

    @Override
    public List<DadosImovelDTO> obterListaDeDados() {
        List<DadosImovel> dados = dir.findAll();
        return dados.stream().map(dado ->
                new DadosImovelDTO(dado.getAnfitriaoId(), dado.getDescricao(),
                        dado.getLocalizacao(), dado.getQuartos())).toList();
    }
}
