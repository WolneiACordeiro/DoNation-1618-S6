package com.fatec.donation.services.impl;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.JoinGroup;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.GroupMapper;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.GroupService;
import com.fatec.donation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.UnauthorizedException;
import org.neo4j.exceptions.EntityNotFoundException;
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
    private final JoinGroupRepository joinGroupRepository;

    @Override
    public GroupDTO createGroup(CreateGroupRequest request) {
        UUID userId = userService.getUserIdByJwt();
        User user = userRepository.findUserById(userId);
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAddress(request.getAddress());
        group.setCreatedAt(LocalDateTime.now());
        group.setOwner(user);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.entityToDto(savedGroup);
    }

    @Override
    public GroupDTO updateGroup(UUID groupId, UpdateGroupRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        UUID userId = userService.getUserIdByJwt();
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para atualizar este grupo");
        }
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAddress(request.getAddress());
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.entityToDto(updatedGroup);
    }

    @Override
    public void deleteGroup(UUID groupId) {
        UUID userId = userService.getUserIdByJwt();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para excluir este grupo");
        }
        groupRepository.delete(group);
    }

    @Override
    public void createJoinRequest(UUID groupId) {
        UUID userId = userService.getUserIdByJwt();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        if (joinGroupRepository.ownerByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("Você é o proprietário desse grupo");
        }
        if (joinGroupRepository.joinRequestByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("Você já fez uma solicitação para este grupo");
        }

        JoinGroup joinRequest = new JoinGroup();
        joinRequest.setUser(userRepository.findUserById(userId));
        joinRequest.setGroup(group);
        joinGroupRepository.save(joinRequest);
    }

    @Override
    public void acceptJoinRequest(UUID requestId) {
        JoinGroup joinRequest = joinGroupRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));
        UUID userId = userService.getUserIdByJwt();
        if (!joinRequest.getGroup().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para aceitar/rejeitar esta solicitação");
        }
    }

}
