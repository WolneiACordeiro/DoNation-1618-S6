package com.fatec.donation.services.impl;

import com.fatec.donation.domain.images.UserImages;
import com.fatec.donation.repository.UserImagesRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserImagesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserImagesServiceImpl implements UserImagesService {
    private final UserImagesRepository userImagesRepository;
    private final UserRepository userRepository;

    @Value("${images.upload.dir}")
    private String uploadDir;

    @Value("${images.default.path}")
    private String defaultImagePath;

    public UserImagesServiceImpl(UserImagesRepository userImagesRepository, UserRepository userRepository) {
        this.userImagesRepository = userImagesRepository;
        this.userRepository = userRepository;
    }

    public List<UserImages> listImages() {
        return userImagesRepository.findAll();
    }

    public Optional<UserImages> getImageById(UUID id) {
        return userImagesRepository.findById(id);
    }

    public UserImages updateOrCreateImageForUser(UUID userId, MultipartFile file) throws IOException {
        UserImages userImage;

        // Verifica se o usuário já possui uma imagem
        Optional<UserImages> imageOptional = userImagesRepository.findByUserId(userId);
        userImage = imageOptional.orElseGet(UserImages::new); // Novo nó se o usuário for novo e não tiver imagem

        if (file == null) {
            // Carrega a imagem padrão se o arquivo de imagem não for fornecido
            Path defaultImage = Path.of(defaultImagePath);
            if (Files.exists(defaultImage)) {
                String encryptedName = generateEncryptedName("default_image");
                String fileName = encryptedName + ".jpg";
                Path uploadPath = Path.of(uploadDir);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(defaultImage, filePath, StandardCopyOption.REPLACE_EXISTING);
                userImage.setName(fileName);
                userImage.setImageLink(filePath.toString());
            } else {
                throw new IllegalArgumentException("Imagem padrão não encontrada em: " + defaultImagePath);
            }
        } else {
            // Remove a imagem anterior se ela existir
            if (userImage.getImageLink() != null) {
                Files.deleteIfExists(Path.of(userImage.getImageLink()));
            }

            // Valida a extensão e tamanho do arquivo
            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);
            if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
                throw new IllegalArgumentException("Formato de arquivo inválido. Apenas png, jpg e jpeg são permitidos.");
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("O arquivo é muito grande. O tamanho máximo permitido é 10MB.");
            }

            // Define o novo nome e salva a nova imagem
            String newName = generateEncryptedName(file.getOriginalFilename());
            userImage.setName(newName + "PIC." + extension);
            String encryptedName = generateEncryptedName(originalName);
            String fileName = encryptedName + "." + extension;

            Path uploadPath = Path.of(uploadDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            userImage.setImageLink(filePath.toString());
        }

        return userImagesRepository.save(userImage);
    }


    public void deleteImage(UUID id) {
        UserImages userImage = userImagesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada com o ID: " + id));
        Path imagePath = Path.of(userImage.getImageLink());
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar a imagem: " + e.getMessage());
        }
        userImagesRepository.delete(userImage);
    }

    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private String generateEncryptedName(String originalName) {
        return UUID.randomUUID().toString();
    }
}
