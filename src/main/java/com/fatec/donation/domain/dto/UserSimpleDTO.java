package com.fatec.donation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSimpleDTO {
    private String name;
    private String username;
    private String email;
}

