package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.JoinGroup;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.UUID;

public interface JoinGroupRepository extends Neo4jRepository<JoinGroup, UUID> {
    @Query("MATCH (user:User {id: $user}) " +
            "MATCH (joinRequest:JoinRequest)-[:REQUESTED_BY]->(user) " +
            "MATCH (joinRequest)-[:FOR_GROUP]->(group:Group {id: $group}) " +
            "RETURN COUNT(joinRequest) > 0 AS exists")
    boolean existsByUserIdAndGroupId(UUID user, UUID group);

    @Query("MATCH (user:User {id: $user})<-[:OWNER]-(group:Group {id: $group}) " +
            "RETURN COUNT(group) > 0 AS exists")
    boolean ownerByUserIdAndGroupId(UUID user, UUID group);

    @Query("MATCH (:JoinRequest {id: $joinRequest})-[:FOR_GROUP]->(group:Group) " +
            "RETURN group.id AS groupId")
    UUID findGroupIdByJoinRequestId(UUID joinRequest);

    @Query("MATCH (:JoinRequest {id: $joinRequest})-[:REQUESTED_BY]->(user:User) " +
            "RETURN user.id AS userId")
    UUID findUserIdByJoinRequestId(UUID joinRequest);

}
