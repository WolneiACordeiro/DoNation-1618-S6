package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.Group;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.UUID;

public interface GroupRepository extends Neo4jRepository<Group, UUID> {
    boolean existsByGroupname(String groupName);

    @Query("MATCH (g:Group {groupname: $groupName}) RETURN g.id)")
    UUID findIdByGroupname(String groupName);
}
