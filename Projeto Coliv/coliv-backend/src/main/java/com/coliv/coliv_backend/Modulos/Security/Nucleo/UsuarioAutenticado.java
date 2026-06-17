package com.coliv.coliv_backend.Modulos.Security.Nucleo;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioAutenticado implements UserDetails {
    private final Long id;
    private final String email;
    private final String senhaHash;
    private final String tipo; // aqui se refere ao tipo do usuário, se é "ANFITRIAO" ou "COLEGA"

    public UsuarioAutenticado(Long id, String email, String senhaHash, String tipo) {
        this.id = id;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+tipo));
    }

    @Override
    public @Nullable String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
