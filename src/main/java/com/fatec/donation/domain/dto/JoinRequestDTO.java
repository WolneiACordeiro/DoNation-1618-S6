package com.fatec.donation.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinRequestDTO {
    private UUID id;
    private GroupSimpleDTO group;
    private UserDTO user;

    public JoinRequestDTO() {
    }

    public JoinRequestDTO(UUID id, GroupSimpleDTO group, UserDTO user) {
        this.id = id;
        this.group = group;
        this.user = user;
    }
}
