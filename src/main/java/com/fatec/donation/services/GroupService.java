package com.fatec.donation.services;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface GroupService {
    GroupDTO createGroup(CreateGroupRequest request) throws IOException;
    GroupDTO updateGroup(String groupName, UpdateGroupRequest request, MultipartFile imageFile, MultipartFile landscapeFile) throws IOException;
    void deleteGroup(String groupName);
    void createJoinRequest(String groupName);
    void acceptJoinRequest(String userName, String groupName);
    void rejectJoinRequest(String userName, String groupName);
    void blockJoinRequest(String groupName, String userName);
    void unblockJoinRequest(String groupName, String userName);
    Page<GroupDTO> getAllGroups(Pageable pageable);
}
