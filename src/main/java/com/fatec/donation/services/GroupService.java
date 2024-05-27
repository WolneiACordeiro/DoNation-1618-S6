package com.fatec.donation.services;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;

import java.util.UUID;

public interface GroupService {
    GroupDTO createGroup(CreateGroupRequest request);
    GroupDTO updateGroup(UUID groupId, UpdateGroupRequest request);
    void deleteGroup(UUID groupId);
    void createJoinRequest(UUID groupId);
    void acceptJoinRequest(UUID requestId);
}
