package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.enums.BrazilStates;
import com.fatec.donation.domain.images.GroupImages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupDTO {
    private String name;
    private String groupname;
    private String description;
    private String address;
    private UserDTO owner;
    private GroupImages groupImage;
    private GroupImages landscapeImage;
}
