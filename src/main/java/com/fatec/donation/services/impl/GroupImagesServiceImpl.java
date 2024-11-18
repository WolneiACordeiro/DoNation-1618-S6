package com.fatec.donation.services.impl;

import com.fatec.donation.domain.images.GroupImages;
import com.fatec.donation.repository.GroupImagesRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.GroupImagesService;
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
public class GroupImagesServiceImpl implements GroupImagesService {
    private final GroupImagesRepository groupImagesRepository;

    @Value("${images.groups.dir}")
    private String profileDir;

    @Value("${images.defaultProfileGroup.path}")
    private String defaultImagePath;

    @Value("${images.defaultLandscapeGroup.path}")
    private String defaultLandscapePath;

    public GroupImagesServiceImpl(GroupImagesRepository groupImagesRepository, UserRepository userRepository) {
        this.groupImagesRepository = groupImagesRepository;
    }

    public List<GroupImages> listImages() {
        return groupImagesRepository.findAll();
    }

    public void quickSort(List<GroupImages> images, int low, int high) {
        if (low < high) {
            int pi = partition(images, low, high);
            quickSort(images, low, pi - 1);
            quickSort(images, pi + 1, high);
        }
    }

    private int partition(List<GroupImages> images, int low, int high) {
        GroupImages pivot = images.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (images.get(j).getName().compareTo(pivot.getName()) < 0) {
                i++;
                swap(images, i, j);
            }
        }
        swap(images, i + 1, high);
        return i + 1;
    }

    private void swap(List<GroupImages> images, int i, int j) {
        GroupImages temp = images.get(i);
        images.set(i, images.get(j));
        images.set(j, temp);
    }

    public Optional<GroupImages> getImageById(UUID id) {
        return groupImagesRepository.findById(id);
    }

    public GroupImages updateOrCreateImageForGroup(UUID groupId, MultipartFile file) throws IOException {
        GroupImages groupImage;

        Optional<GroupImages> imageOptional = groupImagesRepository.findByGroupIdProfile(groupId);
        groupImage = imageOptional.orElseGet(GroupImages::new);

        if (file == null) {
            Path defaultImage = Path.of(defaultImagePath);
            if (Files.exists(defaultImage)) {
                String encryptedName = generateEncryptedName("default_image");
                String fileName = encryptedName + ".png";
                Path uploadPath = Path.of(profileDir);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(defaultImage, filePath, StandardCopyOption.REPLACE_EXISTING);
                groupImage.setName(fileName);
                groupImage.setImageLink(filePath.toString());
            } else {
                throw new IllegalArgumentException("Imagem padrão não encontrada em: " + defaultImagePath);
            }
        } else {
            if (groupImage.getImageLink() != null) {
                Files.deleteIfExists(Path.of(groupImage.getImageLink()));
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

            groupImage.setName(name);
            groupImage.setImageLink(filePath.toString());
        }

        return groupImagesRepository.save(groupImage);
    }

    public GroupImages updateOrCreateLandscapeForGroup(UUID userId, MultipartFile file) throws IOException {
        GroupImages groupImage;

        Optional<GroupImages> imageOptional = groupImagesRepository.findByGroupIdLandscape(userId);
        groupImage = imageOptional.orElseGet(GroupImages::new);

        if (file == null) {
            Path defaultImage = Path.of(defaultLandscapePath);
            if (Files.exists(defaultImage)) {
                String encryptedName = generateEncryptedName("default_landscape");
                String fileName = encryptedName + ".png";
                Path uploadPath = Path.of(profileDir);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(defaultImage, filePath, StandardCopyOption.REPLACE_EXISTING);
                groupImage.setName(fileName);
                groupImage.setImageLink(filePath.toString());
            } else {
                throw new IllegalArgumentException("Imagem padrão não encontrada em: " + defaultLandscapePath);
            }
        } else {
            if (groupImage.getImageLink() != null) {
                Files.deleteIfExists(Path.of(groupImage.getImageLink()));
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

            groupImage.setName(name);
            groupImage.setImageLink(filePath.toString());
        }

        return groupImagesRepository.save(groupImage);
    }


    public void deleteImage(UUID id) {
        GroupImages groupImage = groupImagesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada com o ID: " + id));
        Path imagePath = Path.of(groupImage.getImageLink());
        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar a imagem: " + e.getMessage());
        }
        groupImagesRepository.delete(groupImage);
    }

    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private String generateEncryptedName(String originalName) {
        return UUID.randomUUID().toString();
    }
}