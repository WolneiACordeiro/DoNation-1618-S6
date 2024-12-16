package com.fatec.donation.domain.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Node("ChatMessage")
public class ChatMessage {
    @Id
    private UUID id;
    private String message;
    private LocalDateTime timestamp;
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.Direction.OUTGOING)
    private User user1;
    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.Direction.OUTGOING)
    private User user2;
    @Relationship(type = "HAS_MESSAGE", direction = Relationship.Direction.OUTGOING)
    private ChatMessage chatMessage;

    public ChatMessage() {
        this.id = UUID.randomUUID();
    }
}

