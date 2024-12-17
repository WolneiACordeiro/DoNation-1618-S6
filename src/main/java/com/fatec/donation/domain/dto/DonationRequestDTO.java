package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.entity.ChatMessage;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.enums.DonationStatus;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private DonationStatus donationStatus;
}
