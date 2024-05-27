package com.fatec.donation.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UpdateGroupRequest {
    private String name;
    private String description;
    private String address;
}
