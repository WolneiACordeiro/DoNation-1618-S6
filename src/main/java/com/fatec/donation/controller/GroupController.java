package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.services.GroupService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.UnauthorizedException;
import org.neo4j.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/all")
    public ResponseEntity<Page<GroupDTO>> getAllGroups(Pageable pageable) {
        Page<GroupDTO> groups = groupService.getAllGroups(pageable);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@RequestBody CreateGroupRequest request) {
        GroupDTO groupCreated = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupCreated);
    }

    @PutMapping("/{groupName}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable String groupName, @RequestBody UpdateGroupRequest request) {
        try {
            GroupDTO updatedGroup = groupService.updateGroup(groupName, request);
            return ResponseEntity.ok(updatedGroup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{groupName}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String groupName) {
        try {
            groupService.deleteGroup(groupName);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/join/{groupName}")
    public ResponseEntity<Void> createJoinRequest(@PathVariable String groupName) {
        groupService.createJoinRequest(groupName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/block/{groupName}/{userName}")
    public ResponseEntity<Void> blockJoinRequest(@PathVariable String groupName, @PathVariable String userName) {
        groupService.blockJoinRequest(groupName, userName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/unblock/{groupName}/{userName}")
    public ResponseEntity<Void> unblockJoinRequest(@PathVariable String groupName, @PathVariable String userName) {
        groupService.unblockJoinRequest(groupName, userName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/join/{userName}/{groupName}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable String userName, @PathVariable String groupName) {
        groupService.acceptJoinRequest(userName, groupName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/join/{userName}/{groupName}/reject")
    public ResponseEntity<Void> rejectJoinRequest(@PathVariable String userName, @PathVariable String groupName) {
        groupService.rejectJoinRequest(userName, groupName);
        return ResponseEntity.noContent().build();
    }

}
