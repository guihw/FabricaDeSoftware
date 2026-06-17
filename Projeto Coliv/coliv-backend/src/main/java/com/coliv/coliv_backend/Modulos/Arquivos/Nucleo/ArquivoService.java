package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ArquivoService implements IArquivos {

    @Autowired
    private ArquivoRepository repository;

    public List<Arquivo> listar() {
        return repository.findAll();
    }

    @Override
    public ArquivoDTO getArquivo(Long id) {

        Arquivo arquivo = repository.findById(id)
                .orElseThrow(() ->
                        new ArquivoNaoEncontrado(id));

        return new ArquivoDTO(
                arquivo.getId(),
                arquivo.getUrl(),
                arquivo.getType()
        );
    }

    @Override
    public List<ArquivoDTO> getArquivos(List<Long> ids) {

        return repository.findAllById(ids)
                .stream()
                .map(arquivo -> new ArquivoDTO(
                        arquivo.getId(),
                        arquivo.getUrl(),
                        arquivo.getType()
                ))
                .toList();
    }

    public ArquivoDTO criar(ArquivoRequestDTO dto) {

        Arquivo arquivo = new Arquivo(
                dto.url(),
                dto.type()
        );

        repository.save(arquivo);

        return new ArquivoDTO(
                arquivo.getId(),
                arquivo.getUrl(),
                arquivo.getType()
        );
    }

    public ArquivoDTO editar(Long id, ArquivoRequestDTO dto) {

        Arquivo arquivo = repository.findById(id)
                .orElseThrow(() ->
                        new ArquivoNaoEncontrado(id));

        arquivo.setUrl(dto.url());
        arquivo.setType(dto.type());

        repository.save(arquivo);

        return new ArquivoDTO(
                arquivo.getId(),
                arquivo.getUrl(),
                arquivo.getType()
        );
    }

    public void excluir(Long id) {

        repository.findById(id)
                .orElseThrow(() ->
                        new ArquivoNaoEncontrado(id));

        repository.deleteById(id);
    }
}