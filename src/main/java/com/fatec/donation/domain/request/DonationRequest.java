package com.fatec.donation.domain.request;

import com.fatec.donation.domain.entity.ChatMessage;
import com.fatec.donation.domain.entity.Donation;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.enums.DonationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
@Node("DonationRequest")
@Builder
public class DonationRequest {
    @Id
    private UUID id;
    @Relationship(type = "REQUESTED_BY", direction = Relationship.Direction.OUTGOING)
    private User userReceiver;
    @Relationship(type = "FOR_DONOR", direction = Relationship.Direction.OUTGOING)
    private User userDonor;
    @Relationship(type = "REQUESTED_FROM", direction = Relationship.Direction.OUTGOING)
    private Group group;
    private LocalDateTime createdAt;
    private DonationStatus donationStatus;
    @Relationship(type = "DONATION_REQUESTED", direction = Relationship.Direction.OUTGOING)
    private Donation donation;
    @Relationship(type = "HAS_CHAT", direction = Relationship.Direction.OUTGOING)
    private List<ChatMessage> chatMessages = new ArrayList<>();
    public DonationRequest() {
        this.id = UUID.randomUUID();
    }
}
