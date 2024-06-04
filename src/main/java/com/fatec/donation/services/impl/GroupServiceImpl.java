package com.fatec.donation.services.impl;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.GroupMapper;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.JoinGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.exceptions.*;
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

import java.lang.IllegalStateException;
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
    public GroupDTO updateGroup(String groupName, UpdateGroupRequest request) {
        UUID groupId = groupRepository.findIdByGroupname(groupName);
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
    public void deleteGroup(String groupName) {
        UUID userId = userService.getUserIdByJwt();
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para excluir este grupo");
        }
        groupRepository.delete(group);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void createJoinRequest(String groupName) {
        UUID userId = userService.getUserIdByJwt();
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (blockUserJoinRequestRepository.existsByUserIdAndGroupId(userId, groupId)) {
            throw new IllegalStateException("Você se encontra bloqueado nesse grupo");
        }
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
    public void blockJoinRequest(String groupName, String userName) {
        UUID userId = userService.getUserIdByJwt();
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        UUID blockedUserId = userRepository.findIdByUsername(userName);

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
    public void unblockJoinRequest(String groupName, String userName) {
        UUID userId = userService.getUserIdByJwt();
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        UUID unblockedUserId = userRepository.findIdByUsername(userName);

        User blockedUser = userRepository.findById(unblockedUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (!group.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("Você não tem permissão para desbloquear alguém neste grupo");
        }
        if (!blockUserJoinRequestRepository.existsByUserIdAndGroupId(unblockedUserId, groupId)) {
            throw new IllegalStateException("Esse usuário não se encontra bloqueado");
        }
        group.getBlocked().remove(blockedUser);
        groupRepository.save(group);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void acceptJoinRequest(String userName, String groupName) {
        UUID requestId = joinGroupRequestRepository.findJoinRequestIdByUserNameAndGroupName(userName, groupName);
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
    public void rejectJoinRequest(String userName, String groupName) {
        UUID requestId = joinGroupRequestRepository.findJoinRequestIdByUserNameAndGroupName(userName, groupName);
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
