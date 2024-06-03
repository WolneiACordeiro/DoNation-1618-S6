package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.CompleteUserDTO;
import com.fatec.donation.domain.dto.CredentialsDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.mapper.UserMapper;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/auth")
    public ResponseEntity<?> signIn(@Valid @RequestBody CredentialsDTO credentialsDTO) {
        var token = userService.authenticate(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = JwtService.extractTokenFromRequest(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        UserDTO responseUser = userMapper.toUserDTO(user);
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @PutMapping("/complete-register")
    public ResponseEntity<CompleteUserDTO> firstAccess(@Valid @RequestBody CompleteUserRequest request) {
        UUID userId = userService.getUserIdByJwt();
        User user = userService.completeInfosUser(request, userId);
        CompleteUserDTO responseUser = userMapper.toCompleteUserDTO(user);
        return new ResponseEntity<>(responseUser, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile() {
        UUID userId = userService.getUserIdByJwt();
        return userService.getUserProfile(userId);
    }
}
