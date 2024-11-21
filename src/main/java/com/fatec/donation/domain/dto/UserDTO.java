package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.enums.Roles;
import com.fatec.donation.domain.images.UserImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
public class UserDTO {
    private String name;
    private String username;
    private String email;
    private String profileImage;
    private String landscapeImage;

    public UserDTO(String name, String username, String email, String profileImage, String landscapeImage) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.landscapeImage = landscapeImage;
    }

    public UserDTO() {
    }

}
