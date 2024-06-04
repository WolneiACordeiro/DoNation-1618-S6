package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.Group;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GroupRepository extends Neo4jRepository<Group, UUID> {
    boolean existsByGroupname(String groupName);

    @Query("MATCH (g:Group {groupname: $groupName}) RETURN g.id")
    UUID findIdByGroupname(@Param("groupName") String groupName);

    @Query("MATCH (u:User {username: $userName})-[r:BLOCKED]->(g:Group {groupname: $groupName})" +
            "RETURN COUNT(r) > 0 AS blockedRelationExists")
    Boolean blockedByUserNameAndGroupName(@Param("userName") String userName, @Param("groupName") String groupName);

    @Query("MATCH (u:User {username: $userName})-[r:BLOCKED]->(g:Group {groupname: $groupName})" +
            "RETURN COUNT(r) > 0 AS blockedRelationExists")
    Boolean existsByUserIdAndGroupId(@Param("userName") String userName, @Param("groupName") String groupName);

}
