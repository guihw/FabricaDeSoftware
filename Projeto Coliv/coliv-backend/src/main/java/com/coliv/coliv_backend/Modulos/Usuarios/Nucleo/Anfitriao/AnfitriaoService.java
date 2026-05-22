package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.*;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.*;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listar() {
        return anfitriaoRepository.findAll().stream().
                map(usuarios -> new UsuarioDTO(usuarios.getId(), usuarios.getNome(), usuarios.getCpf())).
                toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        return anfitriaoRepository.findById(id).
                map(usuario -> new UsuarioDTO(id, usuario.getNome(), usuario.getCpf())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }

    @Transactional
    public AnfitriaoPostDTO criarAnfitriao(AnfitriaoPostDTO anfitriaoPostDTO) {
        Anfitriao usuario = new Anfitriao(anfitriaoPostDTO.nome(), anfitriaoPostDTO.cpf(),
                                          anfitriaoPostDTO.email(), anfitriaoPostDTO.senha());

        if (anfitriaoRepository.existsByEmail(anfitriaoPostDTO.email())) {
            throw new EmailDeUsuarioExistente(anfitriaoPostDTO.email());
        }

        if (anfitriaoRepository.existsByCpf(anfitriaoPostDTO.cpf())) {
            throw new CPFDeUsuarioExistente();
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = anfitriaoRepository.save(usuario);
        publisher.publishEvent(new UsuarioAnfitriaoCriado(usuario.getId()));

        return anfitriaoPostDTO;
    }

    public AnfitriaoPutDTO editarAnfitriao(Long id, AnfitriaoPostDTO anfitriao) {
        Anfitriao original = anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        if (anfitriao.nome() != null && !anfitriao.nome().isBlank()) {
            original.setNome(anfitriao.nome());
        }
        if (anfitriao.cpf() != null && !anfitriao.cpf().isBlank()) {
            original.setCpf(anfitriao.cpf());
        }
        if (anfitriao.email() != null && !anfitriao.email().isBlank()) {
            original.setEmail(anfitriao.email());
        }
        if (anfitriao.senha() != null && !anfitriao.senha().isBlank()) {
            original.setSenha(passwordEncoder.encode(anfitriao.senha()));
        }

        original = anfitriaoRepository.save(original);

        return new AnfitriaoPutDTO(original.getId(), original.getNome(), original.getCpf(),
                original.getEmail(), original.getSenha());
    }

    public void excluir(Long id) {
        anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        publisher.publishEvent(new AnfitriaoExcluido(id));

        anfitriaoRepository.deleteById(id);
    }

    @Override
    public UsuarioDTO obterUsuario(Long id) {
        return anfitriaoRepository.findById(id).
                map(usuario -> new UsuarioDTO(id , usuario.getNome(), usuario.getEmail())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }

    @Override
    public List<UsuarioDTO> obterListaDeUsuarios() {
        List<Anfitriao> lista = anfitriaoRepository.findAll();
        return lista.stream().map(usuario ->
                new UsuarioDTO (usuario.getId(), usuario.getNome(), usuario.getEmail())).toList();
    }
}
