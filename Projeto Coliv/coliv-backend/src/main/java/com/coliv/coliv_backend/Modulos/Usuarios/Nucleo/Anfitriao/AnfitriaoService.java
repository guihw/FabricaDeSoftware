package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.IUsuario;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class AnfitriaoService implements IUsuario {

    @Autowired
    private AnfitriaoRepository anfitriaoRepository;
    @Autowired
    private ApplicationEventPublisher publisher;

    public List<Anfitriao> listar() {
        return anfitriaoRepository.findAll();
    }

    public Anfitriao buscarPorId(Long id) {
        return anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }

    public Anfitriao criarAnfitriao(AnfitriaoDTO anfitriaoDTO) {
        Anfitriao usuario = new Anfitriao(anfitriaoDTO.nome(), anfitriaoDTO.cpf(),
                                          anfitriaoDTO.email(), anfitriaoDTO.senha(),
                                          anfitriaoDTO.fotoPerfil());

        usuario = anfitriaoRepository.save(usuario);
        publisher.publishEvent(new UsuarioAnfitriaoCriado(usuario.getId()));

        return usuario;
    }

    public Anfitriao editarAnfitriao(Long id, Anfitriao anfitriao) {
        Anfitriao original = anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        if (anfitriao.getNome() != null && !anfitriao.getNome().isBlank()) {
            original.setNome(anfitriao.getNome());
        }
        if (anfitriao.getCpf() != null && !anfitriao.getCpf().isBlank()) {
            original.setCpf(anfitriao.getCpf());
        }
        if (anfitriao.getEmail() != null && !anfitriao.getEmail().isBlank()) {
            original.setEmail(anfitriao.getEmail());
        }
        if (anfitriao.getSenha() != null && !anfitriao.getSenha().isBlank()) {
            original.setSenha(anfitriao.getSenha());
        }
        if (anfitriao.getPreferenciasId() != null) {
            original.setPreferenciasId(anfitriao.getPreferenciasId());
        }
        if (anfitriao.getDadosImoveId() != null) {
            original.setDadosImoveId(anfitriao.getDadosImoveId());
        }

        original.setPossuiPlano(anfitriao.getPossuiPlano());

        if (anfitriao.getFotoPerfil() != null) {
            original.setFotoPerfil(anfitriao.getFotoPerfil());
        }

        return anfitriaoRepository.save(original);
    }

    public void excluir(Long id) {
        anfitriaoRepository.deleteById(id);
    }

    @Override
    public UsuarioDTO getuser(Long id) {
        return anfitriaoRepository.findById(id).
                map(usuario -> new UsuarioDTO(usuario.getNome(), usuario.getEmail())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }
}
