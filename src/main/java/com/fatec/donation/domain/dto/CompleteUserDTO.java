package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.enums.BrazilStates;
import com.fatec.donation.domain.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class CompleteUserDTO {
    private String name;
    private String username;
    private String email;
    private Set<Roles> roles;
    private String phone;
    private LocalDate birthday;
    private BrazilStates state;
    private String city;
    private List<String> tags;
    private Boolean firstAccess;

    public String getFormattedBirthday() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return birthday.format(formatter);
    }

}

