package com.fatec.donation.repository;

import com.fatec.donation.domain.images.DonationImages;
import com.fatec.donation.domain.images.GroupImages;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DonationImagesRepository extends Neo4jRepository<DonationImages, UUID> {
    @Query("MATCH (u:Group {id: $groupId})<-[:PROFILE_IMAGE]-(img:GroupImages) " +
            "RETURN img")
    Optional<DonationImages> findByGroupIdProfile(UUID groupId);
}

