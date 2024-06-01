package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.services.GroupService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.UnauthorizedException;
import org.neo4j.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/authorities")
    public ResponseEntity<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(authentication.getAuthorities().toString());
    }

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@RequestBody CreateGroupRequest request) {
        GroupDTO groupCreated = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupCreated);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable UUID groupId, @RequestBody UpdateGroupRequest request) {
        try {
            GroupDTO updatedGroup = groupService.updateGroup(groupId, request);
            return ResponseEntity.ok(updatedGroup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID groupId) {
        try {
            groupService.deleteGroup(groupId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/join/{groupId}")
    public ResponseEntity<Void> createJoinRequest(@PathVariable UUID groupId) {
        groupService.createJoinRequest(groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/block/{groupId}/{blockedUserId}")
    public ResponseEntity<Void> blockJoinRequest(@PathVariable UUID groupId, @PathVariable UUID blockedUserId) {
        groupService.blockJoinRequest(groupId, blockedUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/join/{requestId}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable UUID requestId) {
        groupService.acceptJoinRequest(requestId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/join/{requestId}/reject")
    public ResponseEntity<Void> rejectJoinRequest(@PathVariable UUID requestId) {
        groupService.rejectJoinRequest(requestId);
        return ResponseEntity.noContent().build();
    }


}
