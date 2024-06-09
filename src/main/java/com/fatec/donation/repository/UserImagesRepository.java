package com.fatec.donation.repository;

import com.fatec.donation.domain.images.UserImages;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserImagesRepository extends Neo4jRepository<UserImages, UUID> {

}
