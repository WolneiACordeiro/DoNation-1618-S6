package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.CompleteUserDTO;
import com.fatec.donation.domain.dto.CredentialsDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.exceptions.DuplicatedTupleException;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/auth")
    public ResponseEntity signIn (@RequestBody CredentialsDTO credentialsDTO){
        var token = userService.authenticate(credentialsDTO.getEmail(), credentialsDTO.getPassword());
        if(token == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity signUp(@Valid @RequestBody CreateUserRequest request){
        try {
        User user = userService.createUser(request);
        UserDTO responseUser = new UserDTO(user.getName(), user.getUsername(), user.getEmail(), user.getRoles());
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
        }catch(DuplicatedTupleException e){
            Map<String, String> jsonResultado = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(jsonResultado);
        }
    }

    @PutMapping("/complete-register")
    public ResponseEntity<CompleteUserDTO> firstAccess(@Valid @RequestBody CompleteUserRequest request) {
        UUID userId = userService.getUserIdByJwt();
        User user = userService.completeInfosUser(request, userId);
        CompleteUserDTO responseUser = new CompleteUserDTO(user.getName(), user.getUsername(), user.getEmail(), user.getRoles(), user.getPhone(), user.getBirthday(), user.getState(), user.getCity(), user.getTags(), user.getFirstAccess()
        );
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
        String token = JwtService.extractTokenFromRequest(request);
            try {
                UUID userId = userService.getUserIdByJwt();
                return userService.getUserProfile(userId);
            } catch (Exception e) {
                log.error("Error retrieving user profile: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

}
