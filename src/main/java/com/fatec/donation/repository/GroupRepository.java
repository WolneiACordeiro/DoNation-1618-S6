package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.Group;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface GroupRepository extends Neo4jRepository<Group, UUID> {
}
