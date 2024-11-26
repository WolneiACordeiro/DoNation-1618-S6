package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.GroupImagesDTO;
import com.fatec.donation.domain.dto.GroupWithJoinDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRequestRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GroupMapper {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Autowired
    JoinGroupRequestRepository joinGroupRequestRepository;


    public GroupDTO toGroupDTO(Group group) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setName(group.getName());
        groupDTO.setGroupname(group.getGroupname());
        groupDTO.setDescription(group.getDescription());
        groupDTO.setAddress(group.getAddress());
        groupDTO.setOwner(userRepository.findOwnerDTOByGroupId(group.getId()));
        groupDTO.setMembers(userService.findTop5UsersWithImages(group.getId()));
        Optional<GroupImagesDTO> profileImageOpt = groupRepository.findByGroupnameAndRelationTypeDTO(group.getGroupname(), "PROFILE_IMAGE");
        profileImageOpt.map(GroupImagesDTO::getName).ifPresent(groupDTO::setGroupImage);
        Optional<GroupImagesDTO> landscapeImageOpt = groupRepository.findByGroupnameAndRelationTypeDTO(group.getGroupname(), "LANDSCAPE_IMAGE");
        landscapeImageOpt.map(GroupImagesDTO::getName).ifPresent(groupDTO::setLandscapeImage);
        groupDTO.setUsers(groupRepository.findTotalUsersInGroupIncludingOwner(group.getId()));
        return groupDTO;
    }

    public GroupWithJoinDTO toGroupWithJoinDTO(GroupDTO groupDTO, UUID userId){
        GroupWithJoinDTO groupWithJoinDTO = new GroupWithJoinDTO();
        groupWithJoinDTO.setName(groupDTO.getName());
        groupWithJoinDTO.setGroupname(groupDTO.getGroupname());
        groupWithJoinDTO.setDescription(groupDTO.getDescription());
        groupWithJoinDTO.setAddress(groupDTO.getAddress());
        groupWithJoinDTO.setOwner(groupDTO.getOwner());
        groupWithJoinDTO.setMembers(groupDTO.getMembers());
        groupWithJoinDTO.setGroupImage(groupDTO.getGroupImage());
        groupWithJoinDTO.setLandscapeImage(groupDTO.getLandscapeImage());
        groupWithJoinDTO.setUsers(groupDTO.getUsers());

        UUID groupId = groupRepository.findIdByGroupname(groupDTO.getGroupname());

        groupWithJoinDTO.setRequest(joinGroupRequestRepository.existsByUserIdAndGroupId(userId, groupId));

        return groupWithJoinDTO;
    }

    public List<GroupWithJoinDTO> toGroupWithJoinDTOList(List<GroupDTO> groups, UUID userId) {
        return groups.stream()
                .map(groupDTO -> toGroupWithJoinDTO(groupDTO, userId))
                .collect(Collectors.toList());
    }

    public List<GroupDTO> toGroupDTOList(List<Group> groups) {
        return groups.stream()
                .map(this::toGroupDTO)
                .collect(Collectors.toList());
    }

    public Group toGroup(CreateGroupRequest request, User owner) throws IOException {
        return Group.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .groupname(createUniqueGroupName(request.getName()))
                .description(request.getDescription())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .groupImage(request.getGroupImage())
                .landscapeImage(request.getLandscapeImage())
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
        group.setGroupImage(request.getGroupImage());
        group.setLandscapeImage(request.getLandscapeImage());
    }
}