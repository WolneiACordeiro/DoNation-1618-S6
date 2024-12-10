package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.GroupWithJoinDTO;
import com.fatec.donation.domain.dto.JoinRequestDTO;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.domain.request.UpdateUserRequest;
import com.fatec.donation.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.UnauthorizedException;
import org.neo4j.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Controller for managing groups")
public class GroupController {
    private final GroupService groupService;

    @Operation(
            summary = "Get the roles of the authenticated user",
            description = "Retrieve the roles assigned to the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Roles retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/authorities")
    public ResponseEntity<String> getAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(authentication.getAuthorities().toString());
    }

    @Operation(
            summary = "Get a pageable list of all groups",
            description = "Retrieve a pageable list of all groups",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - List of groups retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<GroupDTO>> getAllGroups(Pageable pageable) {
        Page<GroupDTO> groups = groupService.getAllGroups(pageable);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @Operation(
            summary = "Create a new group",
            description = "Create a new group with the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "CREATED - Group created successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "422", description = "UNPROCESSABLE ENTITY - Invalid data")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupDTO> createGroup(
            @RequestPart(value = "createGroupRequest") @Valid CreateGroupRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "landscapeFile", required = false) MultipartFile landscapeFile) {
        try {
            GroupDTO groupCreated = groupService.createGroup(request, imageFile, landscapeFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(groupCreated);
        } catch (IOException e) {
            throw new RuntimeException("Error handling file upload", e);
        }
    }


    @Operation(
            summary = "Update an existing group",
            description = "Update the details of an existing group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Group updated successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "403", description = "FORBIDDEN - You are not the owner of this group"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - Group not found"),
                    @ApiResponse(responseCode = "422", description = "UNPROCESSABLE ENTITY - Invalid data")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(value = "/{groupName}")
    public ResponseEntity<GroupDTO> updateGroup(
            @PathVariable String groupName,
            @RequestPart(value = "updateGroupRequest") @Valid UpdateGroupRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "landscapeFile", required = false) MultipartFile landscapeFile) {
        try {
            GroupDTO updatedGroup = groupService.updateGroup(groupName, request, imageFile, landscapeFile);
            return ResponseEntity.ok(updatedGroup);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "Delete an existing group",
            description = "Delete an existing group by its name",
            responses = {
                    @ApiResponse(responseCode = "204", description = "NO CONTENT - Group deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "403", description = "FORBIDDEN - You are not the owner of this group"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - Group not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(value = "/{groupName}")
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

    @Operation(
            summary = "Create a join request for a group",
            description = "Create a join request for a specific group",
            responses = {
                    @ApiResponse(responseCode = "201", description = "CREATED - Join request created successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "403", description = "FORBIDDEN - You are blocked in this group"),
                    @ApiResponse(responseCode = "409", description = "CONFLICT - You have a pending request in this group"),
                    @ApiResponse(responseCode = "409", description = "CONFLICT - You are a member of this group"),
                    @ApiResponse(responseCode = "409", description = "CONFLICT - You are the owner of this group"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - User not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/join/{groupName}")
    public ResponseEntity<Void> createJoinRequest(@PathVariable String groupName) {
        groupService.createJoinRequest(groupName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Block a join request for a group",
            description = "Block a join request for a specific group and user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Join request blocked successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "403", description = "FORBIDDEN - You don't have authority to block this user"),
                    @ApiResponse(responseCode = "409", description = "CONFLICT - This user is already blocked"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - Group or user not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/block/{groupName}/{userName}")
    public ResponseEntity<Void> blockJoinRequest(@PathVariable String groupName, @PathVariable String userName) {
        groupService.blockJoinRequest(groupName, userName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Unblock a join request for a group",
            description = "Unblock a join request for a specific group and user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Join request unblocked successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "403", description = "FORBIDDEN - This user is not blocked"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - Group not found"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - User not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/unblock/{groupName}/{userName}")
    public ResponseEntity<Void> unblockJoinRequest(@PathVariable String groupName, @PathVariable String userName) {
        groupService.unblockJoinRequest(groupName, userName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Accept a join request for a group",
            description = "Accept a join request for a specific group and user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Join request accepted successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - User not found"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - Group not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/join/{userName}/{groupName}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable String userName, @PathVariable String groupName) {
        groupService.acceptJoinRequest(userName, groupName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Reject a join request for a group",
            description = "Reject a join request for a specific group and user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "NO CONTENT - Join request rejected successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - User not found"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - Group not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/join/{userName}/{groupName}/reject")
    public ResponseEntity<Void> rejectJoinRequest(@PathVariable String userName, @PathVariable String groupName) {
        groupService.rejectJoinRequest(userName, groupName);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/search")
    public ResponseEntity<List<GroupWithJoinDTO>> searchGroups(
            @RequestParam(value = "term", required = false) String term
    ) {
        List<GroupWithJoinDTO> groups = groupService.searchGroupsExcludingOwnerOrMember(term);
        return ResponseEntity.ok(groups);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/search/member")
    public ResponseEntity<List<GroupDTO>> searchGroupsMember(
            @RequestParam(value = "term", required = false) String term
    ) {
        List<GroupDTO> groups = groupService.searchGroupsOnlyMember(term);
        return ResponseEntity.ok(groups);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/search/owner")
    public ResponseEntity<List<GroupDTO>> searchGroupsOwner(
            @RequestParam(value = "term", required = false) String term
    ) {
        List<GroupDTO> groups = groupService.searchGroupsOnlyOwner(term);
        return ResponseEntity.ok(groups);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/search/joinRequests/{groupName}")
    public ResponseEntity<List<JoinRequestDTO>> searchGroupsJoinRequests(
            @PathVariable(required = true) String groupName
    ) {
        List<JoinRequestDTO> joins = groupService.searchGroupJoinRequests(groupName);
        return ResponseEntity.ok(joins);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/search/joinRequests/user")
    public ResponseEntity<List<JoinRequestDTO>> searchGroupsJoinRequestsByMe() {
        List<JoinRequestDTO> joins = groupService.searchUserJoinRequests();
        return ResponseEntity.ok(joins);
    }

}