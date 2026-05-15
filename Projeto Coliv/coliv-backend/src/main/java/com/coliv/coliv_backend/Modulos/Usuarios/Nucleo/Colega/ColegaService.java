package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColegaService {
    @Autowired
    private ColegaRepository colegaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


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
            throw new RuntimeException("Password precisa ter 6 caracteres");
        }
        boolean colegaAlreadyExists =
                colegaRepository.existsByEmail(request.getEmail());

        if (colegaAlreadyExists) {
            throw new RuntimeException("Email já registrado");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        Colega colega = new Colega();

        colega.setNome(request.getNome());
        colega.setEmail(request.getEmail().toLowerCase());
        colega.setSenha(encryptedPassword);

        Colega savedColega = colegaRepository.save(colega);

        return new ColegaResponse(
                savedColega.getId(),
                savedColega.getNome(),
                savedColega.getEmail()
        );

    }

    public Colega editarColega(Long id, Colega colega) {

    }

}
