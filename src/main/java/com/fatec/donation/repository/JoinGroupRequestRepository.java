package com.fatec.donation.repository;

import com.fatec.donation.domain.dto.JoinRequestDTO;
import com.fatec.donation.domain.request.JoinGroupRequest;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JoinGroupRequestRepository extends Neo4jRepository<JoinGroupRequest, UUID> {

    @Query("MATCH (user:User {id: $user}) " +
            "MATCH (joinRequest:JoinRequest)-[:REQUESTED_BY]->(user) " +
            "MATCH (joinRequest)-[:FOR_GROUP]->(group:Group {id: $group}) " +
            "RETURN COUNT(joinRequest) > 0 AS exists")
    boolean existsByUserIdAndGroupId(UUID user, UUID group);

    @Query("MATCH (user:User {id: $user})<-[:OWNER]-(group:Group {id: $group}) " +
            "RETURN COUNT(group) > 0 AS exists")
    boolean ownerByUserIdAndGroupId(UUID user, UUID group);

    @Query("MATCH (user:User {id: $user})-[:MEMBER]->(group:Group {id: $group}) " +
            "RETURN COUNT(group) > 0 AS exists")
    boolean memberByUserIdAndGroupId(UUID user, UUID group);

    @Query("MATCH (user:User {username: $userName}) " +
            "MATCH (group:Group {groupname: $groupName}) " +
            "MATCH (joinRequest:JoinRequest)-[:REQUESTED_BY]->(user) " +
            "MATCH (joinRequest)-[:FOR_GROUP]->(group) " +
            "RETURN joinRequest.id AS joinRequestId")
    UUID findJoinRequestIdByUserNameAndGroupName(@Param("userName") String userName,@Param("groupName") String groupName);

    @Query("MATCH (:JoinRequest {id: $joinRequest})-[:FOR_GROUP]->(group:Group) " +
            "RETURN group.id AS groupId")
    UUID findGroupIdByJoinRequestId(UUID joinRequest);

    @Query("MATCH (:JoinRequest {id: $joinRequest})-[:REQUESTED_BY]->(user:User) " +
            "RETURN user.id AS userId")
    UUID findUserIdByJoinRequestId(UUID joinRequest);

    @Query("MATCH (joinRequest:JoinRequest)-[:FOR_GROUP]->(group:Group {id: $groupId}) " +
            "RETURN joinRequest.id AS id, joinRequest.createdAt AS createdAt")
    List<JoinRequestDTO> findJoinRequestDTOByGroupId(UUID groupId);

    @Query("MATCH (joinRequest:JoinRequest)-[:REQUESTED_BY]->(user:User {id: $userId}) " +
            "RETURN joinRequest.id AS id, joinRequest.createdAt AS createdAt")
    List<JoinRequestDTO> findJoinRequestDTOByUserId(@Param("userId") UUID userId);

}
