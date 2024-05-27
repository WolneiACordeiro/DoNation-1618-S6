package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.JoinGroup;
import com.fatec.donation.domain.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.UUID;

public interface JoinGroupRepository extends Neo4jRepository<JoinGroup, UUID> {
    @Query("MATCH (user:User {id: $user}) " +
            "MATCH (joinRequest:JoinRequest)-[:REQUESTED_BY]->(user) " +
            "MATCH (joinRequest)-[:FOR_GROUP]->(group:Group {id: $group}) " +
            "RETURN COUNT(joinRequest) > 0 AS exists")
    boolean joinRequestByUserIdAndGroupId(UUID user, UUID group);

    @Query("MATCH (user:User {id: $user})<-[:OWNER]-(group:Group {id: $group}) " +
            "RETURN COUNT(group) > 0 AS exists")
    boolean ownerByUserIdAndGroupId(UUID user, UUID group);

}
