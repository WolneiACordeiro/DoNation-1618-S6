package com.fatec.donation.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl {
    private final Path rootLocation = Paths.get("caminho/para/diretorio/de/imagens");

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty() || !isImage(file)) {
                throw new StorageException("Falha ao armazenar arquivo vazio ou não é uma imagem.");
            }
            String filename = UUID.randomUUID().toString() + "." + getExtension(file.getOriginalFilename());
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return rootLocation.resolve(filename).toString();
        } catch (IOException e) {
            throw new StorageException("Falha ao armazenar o arquivo.", e);
        }
    }

    private boolean isImage(MultipartFile file) {

        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image");
    }

    private String getExtension(String filename) {

        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
