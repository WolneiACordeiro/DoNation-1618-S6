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

    @Value("${images.profile.dir}")
    private String profileDir;

    @Value("${images.defaultProfile.path}")
    private String defaultImagePath;

    @Value("${images.defaultLandscape.path}")
    private String defaultLandscapePath;

    public UserImagesServiceImpl(UserImagesRepository userImagesRepository, UserRepository userRepository) {
        this.userImagesRepository = userImagesRepository;
    }

    public List<UserImages> listImages() {
        return userImagesRepository.findAll();
    }

    public Optional<UserImages> getImageById(UUID id) {
        return userImagesRepository.findById(id);
    }

    public UserImages updateOrCreateImageForUser(UUID userId, MultipartFile file) throws IOException {
        UserImages userImage;

        Optional<UserImages> imageOptional = userImagesRepository.findByUserIdProfile(userId);
        userImage = imageOptional.orElseGet(UserImages::new);

        if (file == null) {
            Path defaultImage = Path.of(defaultImagePath);
            if (Files.exists(defaultImage)) {
                String encryptedName = generateEncryptedName("default_image");
                String fileName = encryptedName + ".jpg";
                Path uploadPath = Path.of(profileDir);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(defaultImage, filePath, StandardCopyOption.REPLACE_EXISTING);
                userImage.setName(fileName);
                userImage.setImageLink(filePath.toString());
            } else {
                throw new IllegalArgumentException("Imagem padrão não encontrada em: " + defaultImagePath);
            }
        } else {
            if (userImage.getImageLink() != null) {
                Files.deleteIfExists(Path.of(userImage.getImageLink()));
            }

            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);

            if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
                throw new IllegalArgumentException("Formato de arquivo inválido. Apenas png, jpg e jpeg são permitidos.");
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("O arquivo é muito grande. O tamanho máximo permitido é 10MB.");
            }

            String fileName = generateEncryptedName(file.getOriginalFilename()) + "." + extension;
            String name = fileName;

            Path uploadPath = Path.of(profileDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userImage.setName(name);
            userImage.setImageLink(filePath.toString());
        }

        return userImagesRepository.save(userImage);
    }

    public UserImages updateOrCreateLandscapeForUser(UUID userId, MultipartFile file) throws IOException {
        UserImages userImage;

        Optional<UserImages> imageOptional = userImagesRepository.findByUserIdLandscape(userId);
        userImage = imageOptional.orElseGet(UserImages::new);

        if (file == null) {
            Path defaultImage = Path.of(defaultLandscapePath);
            if (Files.exists(defaultImage)) {
                String encryptedName = generateEncryptedName("default_landscape");
                String fileName = encryptedName + ".png";
                Path uploadPath = Path.of(profileDir);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(defaultImage, filePath, StandardCopyOption.REPLACE_EXISTING);
                userImage.setName(fileName);
                userImage.setImageLink(filePath.toString());
            } else {
                throw new IllegalArgumentException("Imagem padrão não encontrada em: " + defaultLandscapePath);
            }
        } else {
            if (userImage.getImageLink() != null) {
                Files.deleteIfExists(Path.of(userImage.getImageLink()));
            }

            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);

            if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
                throw new IllegalArgumentException("Formato de arquivo inválido. Apenas png, jpg e jpeg são permitidos.");
            }
            if (file.getSize() > 10 * 1024 * 1024) { // Verificar o tamanho do arquivo
                throw new IllegalArgumentException("O arquivo é muito grande. O tamanho máximo permitido é 10MB.");
            }

            String fileName = generateEncryptedName(file.getOriginalFilename()) + "." + extension;

            String name = fileName;

            Path uploadPath = Path.of(profileDir);
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userImage.setName(name);
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