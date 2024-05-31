package com.fatec.donation.repository;

import com.fatec.donation.domain.request.BlockUserJoinRequest;
import com.fatec.donation.domain.request.JoinGroupRequest;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.UUID;

public interface BlockUserJoinRequestRepository extends Neo4jRepository<BlockUserJoinRequest, UUID> {

    @Query("MATCH (user:User {id: $user}) " +
            "MATCH (joinBlocked:JoinBlocked)-[:BLOCKED]->(user) " +
            "MATCH (joinBlocked)-[:IN_THIS_GROUP]->(group:Group {id: $group}) " +
            "RETURN COUNT(joinBlocked) > 0 AS exists")
    boolean existsByUserIdAndGroupId(UUID user, UUID group);

}
