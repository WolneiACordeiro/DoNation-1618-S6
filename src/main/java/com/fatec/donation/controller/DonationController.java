package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.DonationDTO;
import com.fatec.donation.domain.dto.GroupDTO;
import com.fatec.donation.domain.dto.GroupWithJoinDTO;
import com.fatec.donation.domain.dto.JoinRequestDTO;
import com.fatec.donation.domain.request.CreateDonationRequest;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.DonationRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.services.DonationService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
@Tag(name = "Donations", description = "Controller for managing donations")
public class DonationController {
    private final DonationService donationService;

    @Operation(
            summary = "Create a new donation",
            description = "Create a new group with the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "CREATED - Group created successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Authentication required"),
                    @ApiResponse(responseCode = "422", description = "UNPROCESSABLE ENTITY - Invalid data")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/{groupName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DonationDTO> createDonation(
            @PathVariable String groupName,
            @RequestPart(value = "createDonationRequest") @Valid CreateDonationRequest request,
            @RequestPart(value = "imageFile", required = true) MultipartFile imageFile) {
        try {
            DonationDTO donationCreated = donationService.createDonation(request, imageFile, groupName);
            return ResponseEntity.status(HttpStatus.CREATED).body(donationCreated);
        } catch (IOException e) {
            throw new RuntimeException("Error handling file upload", e);
        }
    }

    @PostMapping(value = "{donationId}/{groupName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DonationRequest> createDonationRequest(
            @PathVariable UUID donationId,
            @PathVariable String groupName) {
        try {
            DonationRequest donationCreated = donationService.createDonationRequest(donationId, groupName);
            return ResponseEntity.status(HttpStatus.CREATED).body(donationCreated);
        } catch (IOException e) {
            throw new RuntimeException("Error handling request upload", e);
        }
    }


}