package com.fatec.donation.services.impl;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.GroupMapper;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.JoinGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.exceptions.EntityNotFoundException;
import com.fatec.donation.exceptions.IllegalStateException;
import com.fatec.donation.exceptions.UnauthorizedException;
import com.fatec.donation.repository.BlockUserJoinRequestRepository;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRequestRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.GroupService;
import com.fatec.donation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final UserService userService;
    private final GroupMapper groupMapper;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final JoinGroupRequestRepository joinGroupRequestRepository;
    private final BlockUserJoinRequestRepository blockUserJoinRequestRepository;
    private final PlatformTransactionManager transactionManager;

    @Override
    @Transactional(transactionManager = "transactionManager")
    public GroupDTO createGroup(CreateGroupRequest request) {
        UUID userId = userService.getUserIdByJwt();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        Group group = groupMapper.toGroup(request, user);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toGroupDTO(savedGroup);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
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
    @Transactional(transactionManager = "transactionManager")
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
    @Transactional(transactionManager = "transactionManager")
    public void createJoinRequest(UUID groupId) {
        UUID userId = userService.getUserIdByJwt();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (joinGroupRequestRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("Você já é membro ou solicitou entrada neste grupo");
        }
        if (joinGroupRequestRepository.ownerByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("Você já é proprietário desse grupo");
        }
        JoinGroupRequest joinRequest = new JoinGroupRequest();
        joinRequest.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")));
        joinRequest.setGroup(group);
        joinRequest.setCreatedAt(LocalDateTime.now());
        joinGroupRequestRepository.save(joinRequest);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void blockJoinRequest(UUID groupId, UUID blockedUserId) {
        UUID userId = userService.getUserIdByJwt();
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para bloquear alguém neste grupo");
        }
        if (joinGroupRequestRepository.memberByUserIdAndGroupId(blockedUserId, groupId)) {
            throw new IllegalStateException("Você não pode bloquear um membro participante do grupo");
        }
        if (blockUserJoinRequestRepository.existsByUserIdAndGroupId(blockedUserId, groupId)) {
            throw new IllegalStateException("Esse usuário já se encontra bloqueado");
        }
        group.getBlocked().add(blockedUser);
        groupRepository.save(group);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void acceptJoinRequest(UUID requestId) {
        JoinGroupRequest joinRequest = joinGroupRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de entrada em grupo não encontrada"));
        UUID userId = userService.getUserIdByJwt();
        Group group = joinRequest.getGroup();

        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para aceitar esta solicitação");
        }
        group.getMember().add(joinRequest.getUser());
        joinGroupRequestRepository.delete(joinRequest);
        groupRepository.save(group);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void rejectJoinRequest(UUID requestId) {
        JoinGroupRequest joinRequest = joinGroupRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de entrada em grupo não encontrada"));
        UUID userId = userService.getUserIdByJwt();
        Group group = joinRequest.getGroup();
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para rejeitar esta solicitação");
        }
        joinGroupRequestRepository.delete(joinRequest);
    }

}
