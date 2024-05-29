package com.fatec.donation.services.impl;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.JoinGroup;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.GroupMapper;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.exceptions.EntityNotFoundException;
import com.fatec.donation.exceptions.UnauthorizedException;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.GroupService;
import com.fatec.donation.services.UserService;
import jakarta.transaction.Transactional;
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
    private final JoinGroupRepository joinGroupRepository;

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public void acceptJoinRequest(UUID requestId) {

        UUID userId = userService.getUserIdByJwt();
        UUID groupId = joinGroupRepository.findGroupIdByJoinRequestId(requestId);
        UUID memberId = joinGroupRepository.findUserIdByJoinRequestId(requestId);

        if (!joinGroupRepository.ownerByUserIdAndGroupId(userId, groupId)) {
            throw new UnauthorizedException("Você não tem permissão para aceitar/rejeitar esta solicitação");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        group.getMember().add(member);

        joinGroupRepository.deleteById(requestId);

        groupRepository.save(group);
    }

}
