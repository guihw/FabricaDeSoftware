package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Convite.ConviteAceito;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.AcessoNegadoGrupo;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoIDNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoNaoEncontradoUsandoReferencia;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Grupo.GrupoResponseDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.IChatMembros;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.MembroInfoDTO;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.NovoMembroDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro.MembroService;
import com.coliv.coliv_backend.Modulos.Security.Nucleo.UsuarioAutenticado;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GrupoService implements IChatMembros {

    @Autowired
    private GrupoRepository grupoRepository;
    @Autowired
    private MembroService membroService;

    public List<GrupoResponseDTO> listar() {
        return grupoRepository.findAll().stream().map(grupo -> new GrupoResponseDTO(grupo.getId(),
                grupo.getMembros().stream().map(membro -> new MembroInfoDTO(membro.getUsuarioId(),
                        membro.getTipoUsuario())).toList(), grupo.getNomeGrupo())).toList();
    }

    public GrupoResponseDTO buscarPorUsuarioId (Long usuarioId, TipoUsuario tipoUsuario) {
        Grupo grupo;

        if (tipoUsuario == TipoUsuario.ANFITRIAO) {
            grupo = grupoRepository.findByAnfitriaoId(usuarioId).orElseThrow(() -> new
                    GrupoNaoEncontradoUsandoReferencia(usuarioId));
        } else {
            grupo = grupoRepository.findByColegaId(usuarioId).orElseThrow(() -> new
                    GrupoNaoEncontradoUsandoReferencia(usuarioId));
        }

        return new GrupoResponseDTO(grupo.getId(), grupo.getMembros().stream().map(membro -> new MembroInfoDTO
                (membro.getUsuarioId(), membro.getTipoUsuario())).toList(), grupo.getNomeGrupo());
    }

    @Override
    public boolean usuarioPertenceAoAnfitriao(Long usuarioId, Long anfitriaoId) {
        if (usuarioId.equals(anfitriaoId)) {
            return true;
        }

        return grupoRepository.findByAnfitriaoId(anfitriaoId)
                .map(grupo -> grupo.getMembros().stream()
                        .anyMatch(membro -> membro.getUsuarioId().equals(usuarioId)))
                .orElse(false);
    }

    public GrupoResponseDTO editarNome(Long id, String nome) {
        Grupo grupo = grupoRepository.findById(id).orElseThrow(() -> new GrupoIDNaoEncontrado(id));

        if (nome != null && !nome.isBlank()) {
            grupo.setNomeGrupo(nome);
        }
        grupoRepository.save(grupo);

        return new GrupoResponseDTO(grupo.getId(), grupo.getMembros().stream().map(membro -> new MembroInfoDTO
                (membro.getUsuarioId(), membro.getTipoUsuario())).toList(), nome);
    }

    @EventListener
    @Transactional
    void conviteAceitoEvento(ConviteAceito evento) {

        Grupo grupo = grupoRepository.findByAnfitriaoId(evento.dto().anfitriaoId()).orElse(new Grupo.Builder().
                anfitriaoId(evento.dto().anfitriaoId()).
                nomeGrupo("Grupo").
                build());

        if (grupo.getId() == null) {
            grupo = grupoRepository.save(grupo);
        }

        if (grupo.getMembros() == null) {
            Grupo fg = grupo;
            grupo.setMembros(new ArrayList<>(){{
                add(membroService.novoMembro(new NovoMembroDTO(evento.dto().anfitriaoId(), TipoUsuario.ANFITRIAO), fg));
                add(membroService.novoMembro(new NovoMembroDTO(evento.dto().colegaId(), TipoUsuario.COLEGA), fg));
            }});
        } else {
            grupo.addMembro(membroService.novoMembro(new NovoMembroDTO(evento.dto().colegaId(), TipoUsuario.COLEGA),
                    grupo));
        }

        grupoRepository.save(grupo);
    }

    @Transactional
    public void removerMembro(Long grupoId, Long colegaId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new GrupoIDNaoEncontrado(grupoId));

        if (colegaId.equals(grupo.getAnfitriaoId())) {
            throw new AcessoNegadoGrupo();
        }

        UsuarioAutenticado usuario = usuarioAutenticado();
        boolean autorizado = usuario.getId().equals(grupo.getAnfitriaoId()) || usuario.getId().equals(colegaId);

        if (!autorizado) {
            throw new AcessoNegadoGrupo();
        }

        membroService.removerPorGrupoEUsuario(grupoId, colegaId);
    }

    private UsuarioAutenticado usuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication != null && authentication.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            throw new AcessoNegadoGrupo();
        }

        return usuario;
    }
}