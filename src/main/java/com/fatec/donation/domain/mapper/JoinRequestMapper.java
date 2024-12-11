package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.*;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRequestRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JoinRequestMapper {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JoinGroupRequestRepository joinGroupRequestRepository;

    public List<JoinRequestDTO> toJoinRequestDTO(UUID groupId) {
        List<JoinRequestDTO> joins = joinGroupRequestRepository.findJoinRequestDTOByGroupId(groupId);

        return joins.stream()
                .map(join -> {
                    Group group = groupRepository.findGroupByJoinRequestId(join.getId());
                    User user = userRepository.findUserByJoinRequestId(join.getId());

                    return mapToDTO(join.getId(), group, user);
                })
                .collect(Collectors.toList());
    }

    public List<JoinRequestDTO> toJoinRequestDTOByUser(UUID userId) {
        List<JoinRequestDTO> joins = joinGroupRequestRepository.findJoinRequestDTOByUserId(userId);

        return joins.stream()
                .map(join -> {
                    Group group = groupRepository.findGroupByJoinRequestId(join.getId());
                    User user = userRepository.findUserByJoinRequestId(join.getId());

                    return mapToDTO(join.getId(), group, user);
                })
                .collect(Collectors.toList());
    }


    public JoinRequestDTO mapToDTO(UUID joinRequestId, Group group, User user) {

        GroupSimpleDTO groupDTO = new GroupSimpleDTO();
        groupDTO.setName(group.getName());
        groupDTO.setGroupname(group.getGroupname());
        groupDTO.setDescription(group.getDescription());
        groupDTO.setAddress(group.getAddress());
        groupDTO.setOwner(userRepository.findOwnerDTOByGroupId(group.getId()));
        groupDTO.setMembers(userRepository.findTop5UsersByGroupId(group.getId()));
        groupRepository.findByGroupnameAndRelationTypeDTO(group.getGroupname(), "PROFILE_IMAGE")
                .map(GroupImagesDTO::getName)
                .ifPresent(groupDTO::setGroupImage);
        groupRepository.findByGroupnameAndRelationTypeDTO(group.getGroupname(), "LANDSCAPE_IMAGE")
                .map(GroupImagesDTO::getName)
                .ifPresent(groupDTO::setLandscapeImage);
        groupDTO.setUsers(groupRepository.findTotalUsersInGroupIncludingOwner(group.getId()));


        UserSimpleDTO userDTO = new UserSimpleDTO();
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return new JoinRequestDTO(joinRequestId, groupDTO, userDTO);
    }
}
