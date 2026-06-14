package com.coliv.coliv_backend.Modulos.Security.Nucleo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AutenticacaoUserDetailsService userDetailsService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));

        UsuarioAutenticado usuario =
                (UsuarioAutenticado) userDetailsService.loadUserByUsername(dto.email());

        return new LoginResponseDTO(jwtService.gerarToken(usuario), usuario.getId(), usuario.getTipo());
    }
}

record LoginRequestDTO(String email, String senha) {}
record LoginResponseDTO(String token, Long id, String tipo) {}
