package com.fatec.donation.domain.request;

import com.fatec.donation.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CreateGroupRequest {
    private String name;
    private String description;
    private String address;
    private LocalDateTime createdAt;
}
