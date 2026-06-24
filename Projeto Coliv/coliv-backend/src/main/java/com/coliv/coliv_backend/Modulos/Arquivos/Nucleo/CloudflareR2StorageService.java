package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.UUID;

@Service
@Primary // Esta anotação diz ao Spring para usar esta classe em vez do Mock
public class CloudflareR2StorageService implements StorageService {

    @Autowired
    private S3Client s3Client;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    @Override
    public String upload(MultipartFile arquivo) {
        try {
            // 1. Gera um nome único usando UUID para evitar colisão de nomes no bucket
            String extensao = obterExtensao(arquivo.getOriginalFilename());
            String nomeUnico = UUID.randomUUID().toString() + extensao;

            // 2. Monta a requisição para o S3/R2
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nomeUnico)
                    .contentType(arquivo.getContentType())
                    .build();

            // 3. Envia o arquivo para a Cloudflare
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(arquivo.getInputStream(), arquivo.getSize()));

            // 4. Retorna a URL pública onde o arquivo pode ser acessado
            return publicUrl + "/" + nomeUnico;

        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar o upload do arquivo para o R2", e);
        }
    }

    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf("."));
    }
}