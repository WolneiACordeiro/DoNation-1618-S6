package com.fatec.donation.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserImagesDTO {
    private UUID id;
    private String name;
    private String imageLink;

    public UserImagesDTO(UUID id, String name, String imageLink) {
        this.id = id;
        this.name = name;
        this.imageLink = imageLink;
    }

    public UserImagesDTO() {}
}