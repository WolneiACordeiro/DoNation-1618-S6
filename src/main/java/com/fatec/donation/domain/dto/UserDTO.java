package com.fatec.donation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private String name;
    private String username;
    private String email;
    private String userImage;
    private String landscapeImage;
}

