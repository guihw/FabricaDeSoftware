package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoDTO;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.AnfitriaoExcluido;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.UsuarioAnfitriaoCriado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class AnfitriaoService implements IAnfitriao {

    @Autowired
    private AnfitriaoRepository anfitriaoRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    public List<Anfitriao> listar() {
        return anfitriaoRepository.findAll();
    }

    public Anfitriao buscarPorId(Long id) {
        return anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }

    @Transactional
    public Anfitriao criarAnfitriao(AnfitriaoDTO anfitriaoDTO) {
        Anfitriao usuario = new Anfitriao(anfitriaoDTO.nome(), anfitriaoDTO.cpf(),
                                          anfitriaoDTO.email(), anfitriaoDTO.senha(),
                                          anfitriaoDTO.fotoPerfil());

        if (anfitriaoRepository.existsByEmail(anfitriaoDTO.email())) {
            throw new EmailDeUsuarioExistente(anfitriaoDTO.email());
        }

        if (anfitriaoRepository.existsByCpf(anfitriaoDTO.cpf())) {
            throw new CPFDeUsuarioExistente();
        }
//        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
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

        original.setPossuiPlano(anfitriao.getPossuiPlano());

        if (anfitriao.getFotoPerfil() != null) {
            original.setFotoPerfil(anfitriao.getFotoPerfil());
        }

        return anfitriaoRepository.save(original);
    }

    public void excluir(Long id) {
        anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        publisher.publishEvent(new AnfitriaoExcluido(id));

        anfitriaoRepository.deleteById(id);
    }

    @Override
    public UsuarioDTO getuser(Long id) {
        return anfitriaoRepository.findById(id).
                map(usuario -> new UsuarioDTO(usuario.getNome(), usuario.getEmail())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }
}
