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

        // Não esquecer -> Adicionar a validação após completar os testes
        original.setCpf(anfitriao.getCpf());
        original.setNome(anfitriao.getNome());
        original.setEmail(anfitriao.getEmail());
        original.setSenha(anfitriao.getSenha());
        original.setPreferenciasId(anfitriao.getPreferenciasId());
        original.setDadosImoveId(anfitriao.getDadosImoveId());
        original.setFotoPerfil(anfitriao.getFotoPerfil());

        return anfitriaoRepository.save(original);
    }

    public void excluir(Long id) {
        anfitriaoRepository.deleteById(id);
    }

//    @EventListener
//    public void testeDeEvento(UsuarioAnfitriaoCriado event) {
//        System.out.println("Usuário de ID : " + event.anfitriaoId() + " criado . . .");
//    }

    @Override
    public UsuarioDTO getuser(Long id) {
        return anfitriaoRepository.findById(id).
                map(usuario -> new UsuarioDTO(usuario.getNome(), usuario.getEmail())).
                orElseThrow(() -> new UsuarioIDNaoEncontrado(id));
    }
}
