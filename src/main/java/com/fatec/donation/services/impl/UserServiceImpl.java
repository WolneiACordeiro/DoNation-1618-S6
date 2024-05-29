package com.fatec.donation.services.impl;

import com.fatec.donation.client.CursedWordsService;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.exceptions.DuplicatedTupleException;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @ReadOnlyProperty
    public AccessToken authenticate(String email, String password) {
        User user = getByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public User createUser(CreateUserRequest createUserRequest) {
        validateDuplicateEmail(createUserRequest.getEmail());

        List<CursedWord> wordsToCheck = Arrays.asList(
                new CursedWord(createUserRequest.getName()),
                new CursedWord(createUserRequest.getUsername())
        );

        if (containsInappropriateWords(wordsToCheck)) {
            throw new IllegalArgumentException("O nome ou nome de usuário contém palavras impróprias.");
        }

        User newUser = new User();
        newUser.setName(createUserRequest.getName());
        newUser.setUsername(createUserRequest.getUsername());
        newUser.setEmail(createUserRequest.getEmail());
        newUser.setFirstAccess(true);
        newUser.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setRoles(createUserRequest.getRoles());

        userRepository.save(newUser);

        return newUser;
    }

    @Override
    @Transactional
    public User completeInfosUser(CompleteUserRequest completeUserRequest, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        user.setPhone(completeUserRequest.getPhone());
        user.setBirthday(completeUserRequest.getBirthday());
        user.setState(completeUserRequest.getState());
        user.setCity(completeUserRequest.getCity());
        user.setTags(completeUserRequest.getTags());
        user.setFirstAccess(false);

        userRepository.save(user);
        return user;
    }

    @Override
    @ReadOnlyProperty
    public ResponseEntity<UserDTO> getUserProfile(UUID userId) {
        UserDTO userDTO = userRepository.findUserDTOById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @Override
    @ReadOnlyProperty
    public UUID getUserIdByJwt() {
        String token = JwtService.extractTokenFromRequest(request);
        String email = jwtService.getEmailFromToken(token);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado para o token fornecido.");
        }
        return user.getId();
    }

    private void validateDuplicateEmail(String email) {
        if (getByEmail(email) != null) {
            throw new DuplicatedTupleException("Esse email de usuário já se encontra em uso!");
        }
    }

    private boolean containsInappropriateWords(List<CursedWord> words) {
        return words.parallelStream()
                .map(word -> cursedWordsService.isWordInappropriate(word))
                .collect(Collectors.toList())
                .contains(true);
    }
}
