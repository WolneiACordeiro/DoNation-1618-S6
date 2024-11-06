package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.enums.Roles;
import com.fatec.donation.domain.images.UserImages;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class UserDTO {
    private String name;
    private String username;
    private String email;
    private UserImages images;
}
