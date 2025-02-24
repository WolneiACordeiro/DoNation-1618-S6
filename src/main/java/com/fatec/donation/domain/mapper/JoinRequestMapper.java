package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.*;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRequestRepository;
import com.fatec.donation.repository.UserImagesRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JoinRequestMapper {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserImagesRepository userImagesRepository;

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

    public List<JoinRequestDTO> toJoinRequestDTOByUserReceive(UUID userId) {
        List<JoinRequestDTO> joins = joinGroupRequestRepository.findJoinRequestDTOReceiveByUserId(userId);

        return joins.stream()
                .map(join -> {
                    Group group = groupRepository.findGroupByJoinRequestId(join.getId());
                    User user = userRepository.findUserByJoinRequestId(join.getId());

                    return mapToDTO(join.getId(), group, user);
                })
                .collect(Collectors.toList());
    }

    public List<UserDTO> toMembersDTO(List<UserDTO> groups) {
        return groups.stream()
                .map(userDTO -> {
                    String profileImage = userImagesRepository.findProfileImageNameByUserEmail(userDTO.getEmail()).get();
                    if (profileImage != null) {
                        userDTO.setUserImage(profileImage);
                    }

                    String landscapeImage = userImagesRepository.findLandscapeImageNameByUserEmail(userDTO.getEmail()).get();
                    if (landscapeImage != null) {
                        userDTO.setLandscapeImage(landscapeImage);
                    }

                    return userDTO;
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
        List<UserDTO> groups = userRepository.findTop5UsersByGroupId(group.getId());
        groupDTO.setMembers(toMembersDTO(groups));
        Optional<GroupImagesDTO> profileImageOpt = groupRepository.findByGroupnameAndRelationTypeDTO(group.getGroupname(), "PROFILE_IMAGE");
        profileImageOpt.map(GroupImagesDTO::getName).ifPresent(groupDTO::setGroupImage);
        Optional<GroupImagesDTO> landscapeImageOpt = groupRepository.findByGroupnameAndRelationTypeDTO(group.getGroupname(), "LANDSCAPE_IMAGE");
        landscapeImageOpt.map(GroupImagesDTO::getName).ifPresent(groupDTO::setLandscapeImage);
        groupDTO.setUsers(groupRepository.findTotalUsersInGroupIncludingOwner(group.getId()));


//        UserSimpleDTO userDTO = new UserSimpleDTO();
//        userDTO.setName(user.getName());
//        userDTO.setUsername(user.getUsername());
//        userDTO.setEmail(user.getEmail());

        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        Optional<String> userProfile = userImagesRepository.findProfileImageNameByUserEmail(user.getEmail());
        Optional<String> userLandscape = userImagesRepository.findLandscapeImageNameByUserEmail(user.getEmail());
        userDTO.setUserImage(userProfile.get());
        userDTO.setLandscapeImage(userLandscape.get());

        return new JoinRequestDTO(joinRequestId, groupDTO, userDTO);
    }
}
