package com.fatec.donation.services;

import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateUserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User getByEmail(String email);
    User createUser(CreateUserRequest request);
    AccessToken authenticate(String email, String password);
    Long getUserIdByJwt();
    ResponseEntity<UserDTO> getUserProfile(Long userId);
}
