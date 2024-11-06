package com.fatec.donation.domain.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String username;
    private String name;
    private String password;
}
