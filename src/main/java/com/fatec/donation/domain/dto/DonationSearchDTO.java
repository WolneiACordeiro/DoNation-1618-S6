package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.entity.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DonationSearchDTO {
    private UUID id;
    private String name;
    private String description;
    private String address;
    private List<String> tags;
    private LocalDateTime createdAt;
    private UserOwnerDTO donor;
    private String donationImage;
    private HashSet<Date> avaliableDate;
    private String availability;
}
