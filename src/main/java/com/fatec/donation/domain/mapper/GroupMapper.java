package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {
    public GroupDTO entityToDto(Group group) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName(group.getName());
        groupDTO.setDescription(group.getDescription());
        groupDTO.setAddress(group.getAddress());
        groupDTO.setOwner(group.getOwner());
        return groupDTO;
    }

    public Group dtoToEntity(GroupDTO groupDTO) {
        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setDescription(groupDTO.getName());
        group.setAddress(groupDTO.getAddress());
        group.setOwner(groupDTO.getOwner());
        return group;
    }

}