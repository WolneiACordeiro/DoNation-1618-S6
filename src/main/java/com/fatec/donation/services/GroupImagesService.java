package com.fatec.donation.services;

import com.fatec.donation.domain.images.GroupImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupImagesService {
    List<GroupImages> listImages();

    Optional<GroupImages> getImageById(UUID id);

    GroupImages updateOrCreateImageForGroup(UUID groupId, MultipartFile file) throws IOException;

    GroupImages updateOrCreateLandscapeForGroup(UUID groupId, MultipartFile file) throws IOException;

    void quickSort(List<GroupImages> images, int low, int high);

    void deleteImage(UUID id);

}
