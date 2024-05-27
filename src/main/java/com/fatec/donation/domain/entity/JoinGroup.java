package com.fatec.donation.domain.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Node("JoinRequest")
@Builder
public class JoinGroup {
    @Id
    private UUID id;
    @Relationship(type = "REQUESTED_BY", direction = Relationship.Direction.OUTGOING)
    private User user;
    @Relationship(type = "FOR_GROUP", direction = Relationship.Direction.OUTGOING)
    private Group group;
    public JoinGroup() {
        this.id = UUID.randomUUID();
    }
}
