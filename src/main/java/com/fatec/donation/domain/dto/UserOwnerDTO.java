package com.fatec.donation.domain.dto;

import lombok.Data;

@Data
public class UserOwnerDTO {
    private String name;
    private String username;
    private String email;

    public UserOwnerDTO(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public UserOwnerDTO() {
    }

}
