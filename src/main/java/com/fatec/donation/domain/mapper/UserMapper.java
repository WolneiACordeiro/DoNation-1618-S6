package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.CompleteUserDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class UserMapper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserImagesService userImagesService;

    public User toUser(CreateUserRequest request) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .username(createUniqueUserName(request.getUsername()))
                .email(request.getEmail())
                .userImage(request.getUserImage())
                .firstAccess(true)
                .password(request.getPassword())
                .createdAt(LocalDateTime.now())
                .roles(request.getRoles())
                .build();
    }

    public String createUniqueUserName(String name) {
        String normalizedBaseName = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String baseName = "@" + pattern.matcher(normalizedBaseName).replaceAll("").trim().replace(" ", "").toLowerCase();
        String uniqueName = baseName;
        Random random = new Random();
        while (userRepository.existsByUsername(uniqueName)) {
            String randomSuffix = random.ints(48, 122)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(5)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            uniqueName = baseName + randomSuffix;
        }
        return uniqueName;
    }

    public UserDTO toUserDTO(User user) {
        return new UserDTO(user.getName(), user.getUsername(), user.getEmail(), user.getUserImage(), user.getRoles());
    }

    public CompleteUserDTO toCompleteUserDTO(User user) {
        return new CompleteUserDTO(user.getName(), user.getUsername(), user.getEmail(), user.getRoles(), user.getPhone(), user.getBirthday(), user.getState(), user.getCity(), user.getTags(), user.getFirstAccess());
    }

    public void updateUserWithCompleteInfo(User user, CompleteUserRequest request) {
        user.setPhone(request.getPhone());
        user.setBirthday(request.getBirthday());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setTags(request.getTags());
        user.setFirstAccess(false);
    }
}
