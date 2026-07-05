package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import com.coliv.coliv_backend.Modulos.Arquivos.Contratos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
class ArquivoService implements IArquivos {

    @Autowired
    private ArquivoRepository repository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ExecutorService uploadExecutor;

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
    public List<ArquivoDTO> upload(
            MultipartFile[] arquivos
    ) {

        if (arquivos.length > 10) {
            throw new QuantidadeArquivosInvalida();
        }

        for (MultipartFile arquivo : arquivos) {
            validarArquivo(arquivo);
        }

        List<CompletableFuture<String>> futuros = Arrays.stream(arquivos)
                .map(arquivo -> CompletableFuture.supplyAsync(
                        () -> storageService.upload(arquivo), uploadExecutor))
                .toList();

        List<String> urls = futuros.stream()
                .map(CompletableFuture::join)
                .toList();

        List<ArquivoDTO> resultado = new ArrayList<>();

        for (String url : urls) {

            Arquivo entidade =
                    new Arquivo(
                            url,
                            TipoArquivo.IMAGEM
                    );

            repository.save(entidade);

            resultado.add(
                    new ArquivoDTO(
                            entidade.getId(),
                            entidade.getUrl(),
                            entidade.getType()
                    )
            );
        }

        return resultado;
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
    private void validarArquivo(
            MultipartFile arquivo
    ) {

        long limite =
                5 * 1024 * 1024;

        if (arquivo.getSize() > limite) {

            throw new TamanhoArquivoInvalido(
                    arquivo.getOriginalFilename()
            );
        }

        String contentType =
                arquivo.getContentType();

        if (contentType == null
                || (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png"))) {

            throw new TipoArquivoInvalido(
                    contentType
            );
        }
    }
}