package com.fatec.donation.domain.request;

import com.fatec.donation.domain.enums.BrazilStates;
import com.fatec.donation.domain.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CompleteUserRequest {
    private String phone;
    private LocalDate birthday;
    private BrazilStates state;
    private String city;
    private List<String> tags;
}
