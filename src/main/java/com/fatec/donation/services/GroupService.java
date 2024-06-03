package com.fatec.donation.services;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;

import java.util.UUID;

public interface GroupService {
    GroupDTO createGroup(CreateGroupRequest request);
    GroupDTO updateGroup(String groupName, UpdateGroupRequest request);
    void deleteGroup(String groupName);
    void createJoinRequest(String groupName);
    void acceptJoinRequest(UUID requestId);
    void rejectJoinRequest(UUID requestId);
    void blockJoinRequest(String groupName, String userName);
    void unblockJoinRequest(String groupName, String userName);
}
