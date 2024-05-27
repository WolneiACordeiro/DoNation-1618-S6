package com.fatec.donation.domain.request;

import com.fatec.donation.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class JoinGroupRequest {
    private Set<User> member;
}
