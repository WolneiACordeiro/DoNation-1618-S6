package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.services.GroupService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Group> createGroup(@RequestBody Group request) {
        Group groupCreated = service.createGroup(request);
        return new ResponseEntity<>(groupCreated, HttpStatus.CREATED);
    }

//    @PostMapping("/join/{groupId}/{userId}")
//    public ResponseEntity<RelationshipGroupWantJoin> joinGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
//        RelationshipGroupWantJoin groupCreated = service.joinGroup(groupId, userId);
//        return new ResponseEntity<>(groupCreated, HttpStatus.CREATED);
//    }
//
//    @PostMapping("/accept/{groupId}/{userId}")
//    public ResponseEntity<UserRelationsDTO> acceptGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
//        UserRelationsDTO joinAccepted = service.acceptGroup(groupId, userId);
//        return new ResponseEntity<>(joinAccepted, HttpStatus.ACCEPTED);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<GroupDTO>> getAllGroups() {
//        List<GroupDTO> userDTOs = service.getAllGroups();
//        return new ResponseEntity<>(userDTOs, HttpStatus.OK);

}