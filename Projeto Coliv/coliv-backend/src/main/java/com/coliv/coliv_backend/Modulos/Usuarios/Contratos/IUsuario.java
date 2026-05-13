package com.coliv.coliv_backend.Modulos.Usuarios.Contratos;

public interface IUsuario {
    public UsuarioDTO getuser(Long id);

    public void adicionarPreferenciaAnfitriao(Long userId, Long id);
}
