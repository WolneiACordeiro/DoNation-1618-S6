package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class GroupMapper {

    public GroupDTO toGroupDTO(Group group) {
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

    public Group toGroup(CreateGroupRequest request, User owner) {
        return Group.builder()
        .id(UUID.randomUUID())
        .name(request.getName())
        .description(request.getDescription())
        .address(request.getAddress())
        .createdAt(LocalDateTime.now())
        .owner(owner)
        .build();
    }

    public void updateGroupWithRequest(Group group, UpdateGroupRequest request) {
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAddress(request.getAddress());
    }
}