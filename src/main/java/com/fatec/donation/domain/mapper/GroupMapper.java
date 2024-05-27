package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {
    public GroupDTO entityToDto(Group group) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName(group.getName());
        groupDTO.setDescription(group.getDescription());
        groupDTO.setAddress(group.getAddress());
        User user = group.getOwner();
        UserDTO userDTO = new UserDTO(
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
        groupDTO.setOwner(userDTO);
        return groupDTO;
    }

}