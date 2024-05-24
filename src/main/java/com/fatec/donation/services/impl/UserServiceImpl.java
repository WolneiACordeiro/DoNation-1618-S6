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

import java.util.Arrays;
import java.util.List;

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

        List<String> wordsToCheck = Arrays.asList(request.getName(), request.getUsername());
        boolean isAnyWordInappropriate = wordsToCheck.parallelStream()
                .map(word -> {
                    CursedWord requestData = new CursedWord();
                    requestData.setText(word);
                    ResponseData responseData = cursedWordsService.postEndpointData(requestData);
                    return responseData.getInappropriate();
                })
                .anyMatch(b -> b == true);

        System.out.println("Inappropriate: " + isAnyWordInappropriate);

        if (!isAnyWordInappropriate) {
            userRepository.save(user);
        }

        return user;
    }

}
