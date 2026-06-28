package com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioDTO;

import java.util.List;
import java.util.Optional;

public interface IAnfitriao {

    public UsuarioDTO obterUsuario(Long id);

    public boolean verificarExistencia(Long id);
    
    public List<UsuarioDTO> obterListaDeUsuarios();
    Optional<AnfitriaoCredenciaisDTO> buscarCredenciais(String email);
    void ativarPlano(Long id);
}
