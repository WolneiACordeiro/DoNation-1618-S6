package com.fatec.donation.repository;

import com.fatec.donation.domain.images.UserImages;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserImagesRepository extends Neo4jRepository<UserImages, UUID> {
    @Query("MATCH (u:User {id: $userId})<-[:PROFILE_IMAGE]-(img:UserImages) " +
            "RETURN img")
    Optional<UserImages> findByUserIdProfile(UUID userId);

    @Query("MATCH (u:User {id: $userId})<-[:LANDSCAPE_IMAGE]-(img:UserImages) " +
            "RETURN img")
    Optional<UserImages> findByUserIdLandscape(UUID userId);
}

