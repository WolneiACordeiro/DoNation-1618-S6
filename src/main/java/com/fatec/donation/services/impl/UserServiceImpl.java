package com.fatec.donation.services.impl;

import com.fatec.donation.client.CursedWordsService;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseData;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.exceptions.DuplicatedTupleException;
import com.fatec.donation.exceptions.EntityNotFoundException;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.mapper.UserMapper;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CursedWordsService cursedWordsService;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final UserMapper userMapper;

    @Override
    public User getByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public AccessToken authenticate(String email, String password) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Falha na autenticação.");
        }
        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public User createUser(CreateUserRequest createUserRequest) {
        validateDuplicateEmail(createUserRequest.getEmail());
        validateInappropriateContent(createUserRequest);

        User newUser = userMapper.toUser(createUserRequest);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    @Transactional
    public User completeInfosUser(CompleteUserRequest completeUserRequest, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        userMapper.updateUserWithCompleteInfo(user, completeUserRequest);
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<UserDTO> getUserProfile(UUID userId) {
        return userRepository.findUserDTOById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuário não encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getUserIdByJwt() {
        String token = JwtService.extractTokenFromRequest(request);
        String email = jwtService.getEmailFromToken(token);
        return getByEmail(email).getId();
    }

    // Métodos auxiliares

    private void validateDuplicateEmail(String email) {
        Optional<User> existingUser = userRepository.findUserByEmail(email);
        if (existingUser.isPresent()) {
            throw new DuplicatedTupleException("Esse email de usuário já se encontra em uso!");
        }
    }

    private void validateInappropriateContent(CreateUserRequest request) {
        List<CursedWord> wordsToCheck = buildCursedWordsList(request.getName(), request.getUsername());
        if (containsInappropriateWords(wordsToCheck)) {
            throw new IllegalArgumentException("O nome ou nome de usuário contém palavras impróprias.");
        }
    }

    private List<CursedWord> buildCursedWordsList(String... words) {
        return Stream.of(words)
                .map(CursedWord::new)
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "cursedWordsService", fallbackMethod = "fallbackForInappropriateWords")
    @Retry(name = "cursedWordsService")
    private boolean containsInappropriateWords(List<CursedWord> words) {
        return words.stream()
                .anyMatch(word -> {
                    ResponseData responseData = cursedWordsService.isWordInappropriate(word);
                    return responseData.isInappropriate();
                });
    }

    private boolean fallbackForInappropriateWords(List<CursedWord> words, Throwable t) {
        return false;
    }
}