package com.fatec.donation.services.impl;

import com.fatec.donation.client.CursedWordsService;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseCursedWord;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.images.UserImages;
import com.fatec.donation.domain.mapper.UserMapper;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.domain.request.UpdateUserRequest;
import com.fatec.donation.exceptions.DuplicatedTupleException;
import com.fatec.donation.exceptions.EntityNotFoundException;
import com.fatec.donation.exceptions.IllegalArgumentException;
import com.fatec.donation.exceptions.ResourceNotFoundException;
import com.fatec.donation.jwt.JwtService;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserImagesService;
import com.fatec.donation.services.UserService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CursedWordsService cursedWordsService;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final UserMapper userMapper;
    private final PlatformTransactionManager transactionManager;
    private  final UserImagesService userImagesService;

    @Override
    @Cacheable(value = "usersByEmail", key = "#email")
    public User getByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    @Override
    @Cacheable(value = "usersByUsername", key = "#username")
    public User getByUsername(String username) {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "transactionManager")
    public AccessToken authenticate(String email, String password) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Falha na autenticação.");
        }
        AccessToken accessToken = jwtService.generateToken(user);
        if (jwtService.isTokenBlacklisted(accessToken.getAccessToken())) {
            throw new IllegalArgumentException("Token inválido.");
        }
        return jwtService.generateToken(user);
    }
    @Override
    @Transactional(transactionManager = "transactionManager")
    public void logout(String token) {
        jwtService.blacklistToken(token);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public User createUser(CreateUserRequest createUserRequest) throws NoSuchAlgorithmException, IOException {
        if (isEmailAlreadyExists(createUserRequest.getEmail())) {
            throw new DuplicatedTupleException("Esse email de usuário já se encontra em uso!");
        }
        if (isUsernameAlreadyExists(createUserRequest.getUsername())) {
            throw new DuplicatedTupleException("Esse nome de usuário já se encontra em uso!");
        }
        validateInappropriateContent(createUserRequest);
        createUserRequest.setUserImage(userImagesService.updateOrCreateImageForUser(null, null));
        createUserRequest.setLandscapeImage(userImagesService.updateOrCreateLandscapeForUser(null, null));
        User newUser = userMapper.toUser(createUserRequest);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public User completeInfosUser(CompleteUserRequest completeUserRequest, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        if(Boolean.FALSE.equals(user.getFirstAccess())){
            throw new IllegalArgumentException("Esse não é mais o primeiro acesso do usuário.");
        }
        userMapper.updateUserWithCompleteInfo(user, completeUserRequest);
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public User updateUser(UUID userId, UpdateUserRequest updateUserRequest, MultipartFile imageFile, MultipartFile landscapeFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (updateUserRequest.getUsername() != null && !updateUserRequest.getUsername().equals(user.getUsername())) {
            if (isUsernameAlreadyExists("@" + updateUserRequest.getUsername())) {
                throw new DuplicatedTupleException("Esse nome de usuário já se encontra em uso!");
            }
            user.setUsername("@" + updateUserRequest.getUsername());
        }

        if (updateUserRequest.getName() != null) {
            user.setName(updateUserRequest.getName());
        }

        if (updateUserRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            UserImages userImage = user.getUserImage();
            if (userImage == null) {
                userImage = new UserImages();
                user.setUserImage(userImage);
            }

            userImage = userImagesService.updateOrCreateImageForUser(userId, imageFile);
            user.setUserImage(userImage);
        }

        if (landscapeFile != null && !landscapeFile.isEmpty()) {
            UserImages userImage = user.getUserImage();
            if (userImage == null) {
                userImage = new UserImages();
                user.setLandscapeImage(userImage);
            }

            userImage = userImagesService.updateOrCreateLandscapeForUser(userId, landscapeFile);
            user.setLandscapeImage(userImage);
        }

        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "userProfiles", key = "#userId")
    @Transactional(readOnly = true, transactionManager = "transactionManager")
    public ResponseEntity<UserDTO> getUserProfile(UUID userId) {
        return userRepository.findUserDTOById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de usuário não encontrado."));
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "transactionManager")
    public UUID getUserIdByJwt() {
        String token = JwtService.extractTokenFromRequest(request);
        String email = jwtService.getEmailFromToken(token);
        return getByEmail(email).getId();
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "transactionManager")
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + userId));
    }

    // Métodos auxiliares

    @Cacheable(value = "emailExists", key = "#email")
    private boolean isEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Cacheable(value = "usernameExists", key = "#username")
    private boolean isUsernameAlreadyExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private void validateInappropriateContent(CreateUserRequest request) {
        List<CursedWord> wordsToCheck = Arrays.asList(
                new CursedWord(request.getName()),
                new CursedWord(request.getUsername())
        );

        List<CompletableFuture<Boolean>> futures = wordsToCheck.stream()
                .map(word -> CompletableFuture.supplyAsync(() -> isInappropriateWord(word)))
                .toList();

        boolean hasInappropriateWord = futures.stream()
                .anyMatch(CompletableFuture::join);

        if (hasInappropriateWord) {
            throw new IllegalArgumentException("O nome ou nome de usuário contém palavras impróprias.");
        }
    }

    @CircuitBreaker(name = "cursedWordsService", fallbackMethod = "fallbackForInappropriateWord")
    @Retry(name = "cursedWordsService")
    private boolean isInappropriateWord(CursedWord word) {
        try {
            ResponseCursedWord responseCursedWord = cursedWordsService.isWordInappropriate(word);
            return responseCursedWord.getInapropriado();
        } catch (FeignException ex) {
            return false;
        }
    }

    private boolean fallbackForInappropriateWord(CursedWord word, Throwable t) {
        return false;
    }
}