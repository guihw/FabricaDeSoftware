package com.coliv.coliv_backend.Modulos.Usuarios.Nucleo.Colega;

import com.coliv.coliv_backend.Modulos.Usuarios.Contratos.UsuarioIDNaoEncontrado;
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
    public Colega createColega(){
        
    }
}
