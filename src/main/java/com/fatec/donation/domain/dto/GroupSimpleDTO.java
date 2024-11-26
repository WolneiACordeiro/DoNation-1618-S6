package com.fatec.donation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupSimpleDTO {
    private String name;
    private String groupname;
    private String description;
    private String address;
    private UserOwnerDTO owner;
    private String groupImage;
    private String landscapeImage;
    private Long users;
}

