package com.coliv.coliv_backend.Modulos.Security.Nucleo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Login e geração de token JWT")
@RestController
@RequestMapping("/auth")
@CrossOrigin("https://fabrica-de-software-eight.vercel.app")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AutenticacaoUserDetailsService userDetailsService;
    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Autenticar usuário", description = "Valida credenciais e retorna um token JWT junto com o ID e tipo do usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @SecurityRequirements
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
