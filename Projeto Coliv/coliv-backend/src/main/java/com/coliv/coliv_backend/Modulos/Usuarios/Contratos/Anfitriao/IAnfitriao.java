package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;

import java.util.List;

public interface IAnfitriao {

    public UsuarioDTO obterUsuario(Long id);

    public boolean verificarExistencia(Long id);
    
    public List<UsuarioDTO> obterListaDeUsuarios();
}
