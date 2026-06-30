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
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
class AnfitriaoService implements IAnfitriao {

    @Autowired
    private AnfitriaoRepository anfitriaoRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    @org.springframework.context.annotation.Lazy
    private com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega iColega;

    public List<UsuarioDTO> listar() {
        return anfitriaoRepository.findAll().stream().
                map(u -> new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), u.isPossuiPlano(), u.getFotoPerfilId())).
                toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        return anfitriaoRepository.findById(id).
                map(u -> new UsuarioDTO(id, u.getNome(), u.getEmail(), u.isPossuiPlano(), u.getFotoPerfilId())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }

    @Transactional
    public AnfitriaoPostDTO criarAnfitriao(AnfitriaoPostDTO anfitriaoPostDTO) {
        String emailLower = anfitriaoPostDTO.email() != null ? anfitriaoPostDTO.email().toLowerCase() : "";
        Anfitriao usuario = new Anfitriao(anfitriaoPostDTO.nome(), anfitriaoPostDTO.cpf(),
                                          emailLower, anfitriaoPostDTO.senha());

        if (anfitriaoRepository.existsByEmail(emailLower) || iColega.buscarCredenciais(emailLower).isPresent()) {
            throw new EmailDeUsuarioExistente(emailLower);
        }

        if (anfitriaoRepository.existsByCpf(anfitriaoPostDTO.cpf())) {
            throw new CPFDeUsuarioExistente();
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario = anfitriaoRepository.save(usuario);
        publisher.publishEvent(new UsuarioAnfitriaoCriado(usuario.getId()));

        return anfitriaoPostDTO;
    }
    @Override
    public void ativarPlano(Long id) {
        Anfitriao anfitriao = anfitriaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Anfitrião não encontrado. Id: " + id));

        anfitriao.setPossuiPlano(true);
        anfitriaoRepository.save(anfitriao);
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
            original.setEmail(anfitriao.email().toLowerCase());
        }
        if (anfitriao.senha() != null && !anfitriao.senha().isBlank()) {
            original.setSenha(passwordEncoder.encode(anfitriao.senha()));
        }
        if (anfitriao.fotoPerfilId() != null) {
            original.setFotoPerfilId(anfitriao.fotoPerfilId());
        }

        original = anfitriaoRepository.save(original);

        return new AnfitriaoPutDTO(original.getId(), original.getNome(), original.getCpf(),
                original.getEmail(), original.getSenha(), original.getFotoPerfilId());
    }

    public void excluir(Long id) {
        anfitriaoRepository.findById(id).orElseThrow(() -> new UsuarioIDNaoEncontrado(id));

        publisher.publishEvent(new AnfitriaoExcluido(id));

        anfitriaoRepository.deleteById(id);
    }

    @Override
    public UsuarioDTO obterUsuario(Long id) {
        return anfitriaoRepository.findById(id).
                map(u -> new UsuarioDTO(id, u.getNome(), u.getEmail(), u.isPossuiPlano(), u.getFotoPerfilId())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }

    @Override
    public boolean verificarExistencia(Long id) {
        return anfitriaoRepository.existsById(id);
    }

    @Override
    public List<UsuarioDTO> obterListaDeUsuarios() {
        List<Anfitriao> lista = anfitriaoRepository.findAll();
        return lista.stream().map(usuario ->
                new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.isPossuiPlano(), usuario.getFotoPerfilId())).toList();
    }

    @Override
    public Optional<AnfitriaoCredenciaisDTO> buscarCredenciais(String email) {
        return anfitriaoRepository.findByEmail(email)
                .map(a -> new AnfitriaoCredenciaisDTO(a.getId(), a.getEmail(), a.getSenha()));
    }
}
