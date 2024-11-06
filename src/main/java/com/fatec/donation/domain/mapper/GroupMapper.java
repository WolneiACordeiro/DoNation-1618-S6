package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class GroupMapper {

    @Autowired
    GroupRepository groupRepository;

    public GroupDTO toGroupDTO(Group group) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName(group.getName());
        groupDTO.setGroupname(group.getGroupname());
        groupDTO.setDescription(group.getDescription());
        groupDTO.setAddress(group.getAddress());
        User user = group.getOwner();
        UserDTO userDTO = new UserDTO(
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getUserImage(),
                user.getLandscapeImage()
        );
        groupDTO.setOwner(userDTO);
        return groupDTO;
    }

    public Group toGroup(CreateGroupRequest request, User owner) {
        return Group.builder()
        .id(UUID.randomUUID())
        .name(request.getName())
        .groupname(createUniqueGroupName(request.getName()))
        .description(request.getDescription())
        .address(request.getAddress())
        .createdAt(LocalDateTime.now())
        .owner(owner)
        .build();
    }

    public String createUniqueGroupName(String name) {
        String normalizedBaseName = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String baseName = "@" + pattern.matcher(normalizedBaseName).replaceAll("").trim().replace(" ", "").toLowerCase();
        String uniqueName = baseName;
        Random random = new Random();
        while (groupRepository.existsByGroupname(uniqueName)) {
            String randomSuffix = random.ints(48, 122)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(5)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            uniqueName = baseName + randomSuffix;
        }
        return uniqueName;
    }

    public void updateGroupWithRequest(Group group, UpdateGroupRequest request) {
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAddress(request.getAddress());
    }
}