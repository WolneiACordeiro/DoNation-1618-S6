package com.fatec.donation.services.impl;

import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.GroupMapper;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.GroupService;
import com.fatec.donation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final UserService userService;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Override
    public Group createGroup(Group request) {
        UUID userId = userService.getUserIdByJwt();
        User user = userRepository.findUserById(userId);

        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAddress(request.getAddress());
        group.setCreatedAt(LocalDateTime.now());
        group.setOwner(user);

        Group savedGroup = groupRepository.save(group);

//        return groupMapper.entityToDto(savedGroup);
        return group;
    }
}
