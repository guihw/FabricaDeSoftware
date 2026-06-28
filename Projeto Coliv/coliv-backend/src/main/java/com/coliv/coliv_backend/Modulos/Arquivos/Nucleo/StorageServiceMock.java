package com.coliv.coliv_backend.Modulos.Arquivos.Nucleo;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceMock implements StorageService {

    @Override
    public String upload(MultipartFile arquivo) {

        return "https://storage-pendente/"
                + arquivo.getOriginalFilename();
    }
}