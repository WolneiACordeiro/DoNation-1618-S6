package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.enums.DonationStatus;
import lombok.*;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationRequestDTO {
    private UUID id;
    private UserDTO userReceiver;
    private UserDTO userDonor;
    private GroupDTO group;
    private String createdAt;
    private DonationDTO donation;
    private DonationStatus donationStatus;
}
