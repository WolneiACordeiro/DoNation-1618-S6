package com.fatec.donation.controller;

import com.fatec.donation.domain.images.UserImages;
import com.fatec.donation.services.UserImagesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "Images", description = "Controller for managing images")
public class ImageController {

    private final UserImagesService userImagesService;

    @GetMapping("/users/{filename}")
    public ResponseEntity<Resource> getImageUser(@PathVariable String filename) throws IOException {
        File file = new File("src/main/resources/static/images/users/" + filename);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }

    @GetMapping("/groups/{filename}")
    public ResponseEntity<Resource> getImageGroup(@PathVariable String filename) throws IOException {
        File file = new File("src/main/resources/static/images/groups/" + filename);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }

    @GetMapping("/users/images")
    public ResponseEntity<List<UserImages>> listImages() {
        List<UserImages> images = userImagesService.listImages();
        userImagesService.quickSort(images, 0, images.size() - 1);
        return ResponseEntity.ok(images);
    }

}
