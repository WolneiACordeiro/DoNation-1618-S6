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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        Group group = groupMapper.toGroup(request, user);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toGroupDTO(savedGroup);
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
        groupMapper.updateGroupWithRequest(group, request);
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toGroupDTO(updatedGroup);
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

        if (joinGroupRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("Você já é membro ou solicitou entrada neste grupo");
        }

        JoinGroup joinRequest = new JoinGroup();
        joinRequest.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")));
        joinRequest.setGroup(group);
        joinGroupRepository.save(joinRequest);
    }

    @Override
    @Transactional
    public void acceptJoinRequest(UUID requestId) {
        JoinGroup joinRequest = joinGroupRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de entrada em grupo não encontrada"));

        UUID userId = userService.getUserIdByJwt();
        Group group = joinRequest.getGroup();

        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para aceitar esta solicitação");
        }

        group.getMember().add(joinRequest.getUser());
        joinGroupRepository.delete(joinRequest);
        groupRepository.save(group);
    }

}
