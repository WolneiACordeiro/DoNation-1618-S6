package com.fatec.donation.services;

import com.fatec.donation.domain.images.UserImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserImagesService {
    List<UserImages> listImages();

    Optional<UserImages> getImageById(UUID id);

    UserImages updateOrCreateImageForUser(UUID userId, MultipartFile file) throws IOException;

    void deleteImage(UUID id);
}
