package com.fatec.donation.controller;

import com.fatec.donation.domain.dto.CredentialsDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<UserDTO> signUp(@RequestBody CreateUserRequest request){
        User user = userService.createUser(request);
        UserDTO responseUser = new UserDTO(user.getName(), user.getUsername(), user.getEmail(), user.getRoles());
        return new ResponseEntity<>(responseUser, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
        String token = JwtService.extractTokenFromRequest(request);
            try {
                Long userId = userService.getUserIdByJwt();
                return userService.getUserProfile(userId);
            } catch (Exception e) {
                log.error("Error retrieving user profile: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

//    @GetMapping("/user-id")
//    public String getUserId(HttpServletRequest request) {
//        String token = JwtService.extractTokenFromRequest(request);
//        if (token != null) {
//            try {
//                Long userId = userService.getUserIdByJwt();
//                log.info("User ID: {}", userId);
//                return "User ID: " + userId;
//            } catch (Exception e) {
//                log.error("Invalid token: {}", e.getMessage());
//                return "Invalid token";
//            }
//        } else {
//            log.error("No token found in request");
//            return "No token found in request";
//        }
//    }

}
