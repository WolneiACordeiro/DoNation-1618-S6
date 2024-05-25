package com.fatec.donation.domain.request;

import com.fatec.donation.domain.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CreateUserRequest {
    private String name;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private Set<Roles> roles;
}
