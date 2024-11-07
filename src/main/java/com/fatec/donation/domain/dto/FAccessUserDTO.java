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
public class FAccessUserDTO {
    private Boolean firstAccess;
}

