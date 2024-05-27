package com.fatec.donation.services.impl;

import com.fatec.donation.client.CursedWordsService;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;

import com.fatec.donation.exceptions.DuplicatedTupleException;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CursedWordsService cursedWordsService;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    public User getByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public AccessToken authenticate(String email, String password) {
        var user = getByEmail(email);
        if(user == null){
            return null;
        }
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if(matches){
            return jwtService.generateToken(user);
        }
        return null;
    }

    @Override
    public User createUser(CreateUserRequest request){
        var possibleCustomer = getByEmail(request.getEmail());
        if(possibleCustomer != null) {
            throw new DuplicatedTupleException("Esse email de usuário já se encontra em uso!");
        }
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstAccess(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(request.getRoles());

//        List<String> wordsToCheck = Arrays.asList(request.getName(), request.getUsername());
//        boolean isAnyWordInappropriate = wordsToCheck.parallelStream()
//                .map(word -> {
//                    CursedWord requestData = new CursedWord();
//                    requestData.setText(word);
//                    ResponseData responseData = cursedWordsService.postEndpointData(requestData);
//                    return responseData.getInappropriate();
//                })
//                .anyMatch(b -> b == true);
//
//        System.out.println("Inappropriate: " + isAnyWordInappropriate);

//        if (!isAnyWordInappropriate) {
//            userRepository.save(user);
//        }

        userRepository.save(user);

        return user;
    }

    @Override
    public User completeInfosUser(CompleteUserRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        user.setPhone(request.getPhone());
        user.setBirthday(request.getBirthday());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setTags(request.getTags());
        user.setFirstAccess(false);

        userRepository.save(user);

        return user;
    }

    @Override
    public ResponseEntity<UserDTO> getUserProfile(UUID userId) {
        UserDTO user = userRepository.findUserDTOById(userId);
        return ResponseEntity.ok(user);
    }

    @Override
    public UUID getUserIdByJwt() {
        String token = JwtService.extractTokenFromRequest(request);
        String email = jwtService.getEmailFromToken(token);
        User user = userRepository.findUserByEmail(email);
        return user.getId();
    }

}
