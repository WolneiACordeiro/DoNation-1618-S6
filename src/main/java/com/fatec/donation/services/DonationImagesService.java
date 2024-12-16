package com.fatec.donation.services;

import com.fatec.donation.domain.images.DonationImages;
import com.fatec.donation.domain.images.GroupImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DonationImagesService {
    List<DonationImages> listImages();

    Optional<DonationImages> getImageById(UUID id);

    DonationImages updateOrCreateImageForDonation(UUID donationId, MultipartFile file) throws IOException;

    void quickSort(List<DonationImages> images, int low, int high);

    void deleteImage(UUID id);

}
