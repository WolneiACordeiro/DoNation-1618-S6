package com.fatec.donation.services;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.GroupWithJoinDTO;
import com.fatec.donation.domain.dto.JoinRequestDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupDTO createGroup(CreateGroupRequest request, MultipartFile imageFile, MultipartFile landscapeFile) throws IOException;
    GroupDTO updateGroup(String groupName, UpdateGroupRequest request, MultipartFile imageFile, MultipartFile landscapeFile) throws IOException;
    void deleteGroup(String groupName);
    void createJoinRequest(String groupName);
    void acceptJoinRequest(String userName, String groupName);
    void rejectJoinRequest(String userName, String groupName);
    void deleteJoinRequest(String userName);
    void blockJoinRequest(String groupName, String userName);
    void unblockJoinRequest(String groupName, String userName);
    List<GroupWithJoinDTO> searchGroupsExcludingOwnerOrMember(String searchTerm);
    List<GroupDTO> searchGroupsOnlyMember(String searchTerm);
    List<GroupDTO> searchGroupsOnlyOwner(String searchTerm);
    Page<GroupDTO> getAllGroups(Pageable pageable);
    List<JoinRequestDTO> searchGroupJoinRequests(String groupName);
    List<JoinRequestDTO> searchUserJoinRequests();
    List<JoinRequestDTO> searchUserJoinRequestsReceive();
    GroupDTO getGroup(String groupname);
}
