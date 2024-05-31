package com.fatec.donation.services;

import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    User getByEmail(String email);
    User getById(UUID id);
    User createUser(CreateUserRequest request);
    AccessToken authenticate(String email, String password);
    UUID getUserIdByJwt();
    ResponseEntity<UserDTO> getUserProfile(UUID userId);
    User completeInfosUser(CompleteUserRequest request, UUID userId);
}
