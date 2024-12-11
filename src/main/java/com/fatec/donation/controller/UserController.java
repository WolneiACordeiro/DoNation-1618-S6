package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.CompleteUserDTO;
import com.fatec.donation.domain.dto.CredentialsDTO;
import com.fatec.donation.domain.dto.FAccessUserDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.UserMapper;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.domain.request.UpdateUserRequest;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Controller for Users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Authenticate user",
            description = "Authenticate user with email and password and return a JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Authentication successful"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Invalid credentials")
            }
    )

    @PostMapping("/auth")
    public ResponseEntity<?> signIn(@Valid @RequestBody CredentialsDTO credentialsDTO) {
        var token = userService.authenticate(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Logout user",
            description = "Invalidate the JWT token and logout the user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "NO CONTENT - Logout successful"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Invalid token")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = JwtService.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Register a new user",
            description = "Register a new user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "201", description = "CREATED - User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST - Invalid input data")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody CreateUserRequest request) throws NoSuchAlgorithmException, IOException {
        User user = userService.createUser(request);
        UserDTO responseUser = userMapper.toUserDTO(user);
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Check user's first access",
            description = "Verifies if this is the user's first access based on provided information. If it is, the system may require additional data for registration completion.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - First access confirmed successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Invalid or missing token")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/check-access")
    public ResponseEntity<FAccessUserDTO> firstAccessCheckout() {
        UUID userId = userService.getUserIdByJwt();
        User user = userService.getUserById(userId);
        FAccessUserDTO responseUser = userMapper.toFAccessUserDTO(user);
        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }

    @Operation(
            summary = "Complete user registration",
            description = "Complete the user's registration with additional information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Registration completed successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Invalid token")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/complete-register")
    public ResponseEntity<CompleteUserDTO> firstAccess(@Valid @RequestBody CompleteUserRequest request) {
        UUID userId = userService.getUserIdByJwt();
        User user = userService.completeInfosUser(request, userId);
        CompleteUserDTO responseUser = userMapper.toCompleteUserDTO(user);
        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }

    @Operation(
            summary = "Update user details",
            description = "Update user information like email, username, password, and image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST - Invalid input data"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Invalid token"),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND - User not found")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/")
    public ResponseEntity<UserDTO> updateUser(
            @RequestPart(value = "updateUserRequest") @Valid UpdateUserRequest updateUserRequest,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "landscapeFile", required = false) MultipartFile landscapeFile
    ) throws IOException {

        UUID userId = userService.getUserIdByJwt();

        User user = userService.updateUser(userId, updateUserRequest, imageFile, landscapeFile);

        UserDTO responseUser = userMapper.toUserDTO(user);

        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }

    @Operation(
            summary = "Get user profile",
            description = "Retrieve the authenticated user's profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK - Profile retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED - Invalid token")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile() {
        UUID userId = userService.getUserIdByJwt();
        User user = userService.getUserById(userId);
        UserDTO responseUser = userMapper.toUserDTO(user);
        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile/{userName}")
    public ResponseEntity<UserDTO> getOptionalUserProfile(@PathVariable String userName) {
        return new ResponseEntity<>(userService.getOptionalUserProfile(userName), HttpStatus.OK);
    }


}
