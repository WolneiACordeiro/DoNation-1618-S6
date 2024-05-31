package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.CompleteUserDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.request.CompleteUserRequest;
import com.fatec.donation.domain.request.CreateUserRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstAccess(true)
                .password(request.getPassword())
                .createdAt(LocalDateTime.now())
                .roles(request.getRoles())
                .build();
    }

    public UserDTO toUserDTO(User user) {
        return new UserDTO(user.getName(), user.getUsername(), user.getEmail(), user.getRoles());
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
