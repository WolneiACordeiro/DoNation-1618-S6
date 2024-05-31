package com.fatec.donation.domain.request;

import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Node("JoinBlocked")
@Builder
public class BlockUserJoinRequest {
    @Id
    private UUID id;
    @Relationship(type = "BLOCKED", direction = Relationship.Direction.OUTGOING)
    private User user;
    @Relationship(type = "IN_THIS_GROUP", direction = Relationship.Direction.OUTGOING)
    private Group group;
    private LocalDateTime createdAt;
    public BlockUserJoinRequest() {
        this.id = UUID.randomUUID();
    }
}
