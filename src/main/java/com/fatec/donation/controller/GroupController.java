package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.services.GroupService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.UnauthorizedException;
import org.neo4j.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService service;

    @PostMapping("/create")
    public ResponseEntity<GroupDTO> createGroup(@RequestBody CreateGroupRequest request) {
        GroupDTO groupCreated = service.createGroup(request);
        return new ResponseEntity<>(groupCreated, HttpStatus.CREATED);
    }

    @PutMapping("/update/{groupId}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable UUID groupId, @RequestBody UpdateGroupRequest request) {
        try {
            GroupDTO updatedGroup = service.updateGroup(groupId, request);
            return ResponseEntity.ok(updatedGroup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID groupId) {
        try {
            service.deleteGroup(groupId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/join-request/{groupId}")
    public ResponseEntity<Void> createJoinRequest(@PathVariable UUID groupId) {
        service.createJoinRequest(groupId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/join-request/{requestId}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable UUID requestId) {
        service.acceptJoinRequest(requestId);
        return ResponseEntity.noContent().build();
    }

}