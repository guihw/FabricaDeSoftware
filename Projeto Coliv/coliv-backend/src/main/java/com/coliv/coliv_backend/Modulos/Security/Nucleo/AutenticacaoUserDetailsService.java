package com.coliv.coliv_backend.Modulos.Security.Nucleo;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Anfitriao.IAnfitriao;
import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.Colega.IColega;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoUserDetailsService implements UserDetailsService {
    @Autowired
    private IAnfitriao iAnfitriao;
    @Autowired
    private IColega iColega;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var anfitriao = iAnfitriao.buscarCredenciais(email);
        if (anfitriao.isPresent()) {
            var a = anfitriao.get();
            return new UsuarioAutenticado(a.id(), a.email(), a.senhaHash(), "ANFITRIAO");
        }

        var colega = iColega.buscarCredenciais(email);
        if (colega.isPresent()) {
            var c = colega.get();
            return new UsuarioAutenticado(c.id(), c.email(), c.senhaHash(), "COLEGA");
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + email);
    }
}
