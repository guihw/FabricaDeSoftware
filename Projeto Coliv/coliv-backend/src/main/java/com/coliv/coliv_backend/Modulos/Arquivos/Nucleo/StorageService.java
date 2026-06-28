package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(MultipartFile arquivo);

}