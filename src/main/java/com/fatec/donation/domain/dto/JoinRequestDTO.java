package com.fatec.donation.domain.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinRequestDTO {
    private UUID id;
    private GroupDTO group;
    private UserDTO userDTO;

    public JoinRequestDTO() {
    }

    public JoinRequestDTO(UUID id, GroupDTO group, UserDTO userDTO) {
        this.id = id;
        this.group = group;
        this.userDTO = userDTO;
    }
}
