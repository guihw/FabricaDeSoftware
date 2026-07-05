package com.coliv.coliv_backend.Modulos.Chat.Nucleo.Membro;

import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.MembroNaoEncontrado;
import com.coliv.coliv_backend.Modulos.Chat.Contratos.Membro.NovoMembroDTO;
import com.coliv.coliv_backend.Modulos.Chat.Nucleo.Grupo.Grupo;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.TipoUsuario;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembroService {

    @Autowired
    private MembroRepository membroRepository;
    @Autowired
    private IAnfitriao iAnfitriao;
    @Autowired
    private IColega iColega;

    @Transactional
    public Membro novoMembro(NovoMembroDTO evento, Grupo grupo) {

        if (evento.tipoUsuario() == TipoUsuario.ANFITRIAO) {
            if (!iAnfitriao.verificarExistencia(evento.usuarioId())) {
                throw new UsuarioIDNaoEncontrado(evento.usuarioId());
            }
        } else {
            if (!iColega.verificarExistencia(evento.usuarioId())) {
                throw new UsuarioIDNaoEncontrado(evento.usuarioId());
            }
        }

        Membro membro = new Membro.Builder().
                usuarioId(evento.usuarioId()).
                tipoUsuario(evento.tipoUsuario()).
                grupo(grupo).
                build();

        return membroRepository.save(membro);
    }

    @Transactional
    public void removerPorGrupoEUsuario(Long grupoId, Long usuarioId) {
        Membro membro = membroRepository.findByGrupoIdAndUsuarioId(grupoId, usuarioId)
                .orElseThrow(() -> new MembroNaoEncontrado(grupoId, usuarioId));

        membroRepository.delete(membro);
    }
}