package com.fatec.donation.services;

import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.domain.request.UpdateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface UserService {
    User getByEmail(String email);
    User getByUsername(String username);
    User createUser(CreateUserRequest request) throws NoSuchAlgorithmException, IOException;
    AccessToken authenticate(String email, String password);
    UUID getUserIdByJwt();

    @Transactional(transactionManager = "transactionManager")
    User updateUser(UUID userId, UpdateUserRequest updateUserRequest, MultipartFile imageFile) throws IOException;

    ResponseEntity<UserDTO> getUserProfile(UUID userId);
    User completeInfosUser(CompleteUserRequest request, UUID userId);
    void logout(String token);
}
