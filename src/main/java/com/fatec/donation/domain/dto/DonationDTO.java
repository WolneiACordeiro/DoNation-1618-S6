package com.fatec.donation.domain.dto;

import com.fatec.donation.domain.entity.Date;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.images.DonationImages;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DonationDTO {
    private String name;
    private String description;
    private String address;
    private List<String> tags;
    private String createdAt;
    private UserOwnerDTO donor;
    private GroupDTO group;
    private String donationImage;
    private List<Date> avaliableDate;
    private String availability;
}
