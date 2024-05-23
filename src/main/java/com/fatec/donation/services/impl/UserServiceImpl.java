package com.fatec.donation.services.impl;

import com.fatec.donation.client.CursedWordsService;
import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseData;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CursedWordsService cursedWordsService;

    public User createUser(CreateUserRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setRoles(request.getRoles());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        CursedWord requestData = new CursedWord();
        requestData.setText(request.getUsername());
        ResponseData responseData = cursedWordsService.postEndpointData(requestData);
        boolean isTextInappropriate = responseData.getInappropriate();
        System.out.println("Inappropriate: " + isTextInappropriate);

        userRepository.save(user);

        return user;
    }

}
