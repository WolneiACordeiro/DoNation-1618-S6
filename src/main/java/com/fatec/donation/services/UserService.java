package com.fatec.donation.services;

import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateUserRequest;

public interface UserService {
    User createUser(CreateUserRequest request);
}
