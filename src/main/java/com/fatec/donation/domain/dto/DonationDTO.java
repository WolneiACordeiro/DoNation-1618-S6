package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.entity.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DonationDTO {
    private String donationName;
    private String description;
    private String address;
    private List<String> tags;
    private HashSet<Date> avaliableDate;
    private UserOwnerDTO owner;
    private GroupDTO group;
    private String donationImage;
    private String availability;
}

