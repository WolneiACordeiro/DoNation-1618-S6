package com.fatec.donation.services.impl;

import com.fatec.donation.domain.images.UserImages;
import com.fatec.donation.repository.UserImagesRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserImagesService;
import com.fatec.donation.services.UserService;
import com.fatec.donation.utils.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserImagesServiceImpl implements UserImagesService {
    private UserImagesRepository userImagesRepository;
    private UserRepository userRepository;
    private UserService userService;
    private String uploadDir;

    @Value("./images/default/default.jpg")
    private String defaultImagePath;

    @Autowired
    public UserImagesServiceImpl(UserImagesRepository userImagesRepository, @Value("./images/users/") String uploadDir) {
        this.userImagesRepository = userImagesRepository;
        this.uploadDir = uploadDir;
    }

    public List<UserImages> listImages() {
        return userImagesRepository.findAll();
    }

    public Optional<UserImages> getImageById(UUID id) {
        return userImagesRepository.findById(id);
    }

    public UserImages createImage(MultipartFile file, boolean isDefaultImage) throws NoSuchAlgorithmException {
        UserImages userImage = new UserImages();
        if (file == null){
            String filePath = defaultImagePath;
            File localFile = new File(filePath);
            if (localFile.exists() && localFile.isFile()) {
                try {
                    byte[] fileBytes = Files.readAllBytes(localFile.toPath());
                    file = new CustomMultipartFile(fileBytes, localFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Arquivo não encontrado ou não é um arquivo válido.");
            }
        }

        String originalName = file != null ? file.getOriginalFilename() : defaultImagePath;
        String extension = file != null ? extractExtension(file.getOriginalFilename()) : "jpg";
        if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
            throw new IllegalArgumentException("Formato de arquivo inválido. Apenas png, jpg e jpeg são permitidos.");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("O arquivo é muito grande. O tamanho máximo permitido é 10MB.");
        }

        String encryptedName = generateEncryptedName(originalName);
        String fileName = encryptedName + "." + extension;

        userImage.setName(fileName);

        Path uploadPath = Path.of(uploadDir);
        try {
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);

            if (isDefaultImage || file == null) {
                Path defaultImage = Path.of(defaultImagePath);
                Files.copy(defaultImage, filePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            userImage.setImageLink(filePath.toString());

            return userImagesRepository.save(userImage);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar a imagem: " + e.getMessage());
        }
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    public String generateEncryptedName(String originalName) {
        return UUID.randomUUID().toString();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public UserImages updateImage(UUID id, MultipartFile file) throws IOException {
        Optional<UserImages> imageOptional = userImagesRepository.findById(id);
        if (imageOptional.isPresent()) {
            UserImages userImage = imageOptional.get();

            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);
            if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
                throw new IllegalArgumentException("Formato de arquivo inválido. Apenas png, jpg e jpeg são permitidos.");
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                throw new IllegalArgumentException("O arquivo é muito grande. O tamanho máximo permitido é 10MB.");
            }

            String newName = userRepository.getUsernameById(userService.getUserIdByJwt());
            userImage.setName(newName + "PIC." + extension);

            String encryptedName = generateEncryptedName(originalName);
            String fileName = encryptedName + "." + extension;

            Path uploadPath = Path.of(uploadDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            userImage.setImageLink(filePath.toString());
            return userImagesRepository.save(userImage);
        } else {
            throw new IllegalArgumentException("Image not found with ID: " + id);
        }
    }


    public void deleteImage(UUID id) {
        UserImages userImage = userImagesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with ID: " + id));
        Path imagePath = Path.of(userImage.getImageLink());
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            // Handle the exception
        }
        userImagesRepository.delete(userImage);
    }
}
