package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColegaService {
    ColegaRepository colegaRepository;

    public List<Colega> getAllColegas(){
        return colegaRepository.findAll();
    }
    public Colega getColega(Long id){
        return colegaRepository.findById(id).orElseThrow(()-> new UsuarioIDNaoEncontrado(id));
    }
    public ColegaResponse createColega(CreateColegaRequest request){
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new RuntimeException("Nome está faltando");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email está faltando");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must contain at least 6 characters");
        }

        boolean userAlreadyExists =
                colegaRepository.existsByEmail(request.getEmail());

        if (userAlreadyExists) {
            throw new RuntimeException("Email already registered");
        }

        String encryptedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(encryptedPassword);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );

    }
}
