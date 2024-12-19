package com.fatec.donation.domain.entity;

import com.fatec.donation.domain.images.DonationImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Node("Donation")
@Builder
public class Donation {
    @Id
    private UUID id;
    private String name;
    private String description;
    private String address;
    private List<String> tags;
    private LocalDateTime createdAt;
    @Relationship(type = "DONOR", direction = Relationship.Direction.OUTGOING)
    private User donor;
    @Relationship(type = "DONATION_FROM", direction = Relationship.Direction.OUTGOING)
    private Group group;
    @Relationship(type = "DONATION_IMAGE", direction = Relationship.Direction.INCOMING)
    private DonationImages donationImage;
    private HashSet<Date> avaliableDate;
    private String availability;
    public Donation() {
        this.id = UUID.randomUUID();
    }
}
