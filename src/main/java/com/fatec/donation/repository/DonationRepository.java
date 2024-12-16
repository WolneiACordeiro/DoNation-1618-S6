package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.Donation;
import com.fatec.donation.domain.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.UUID;

public interface DonationRepository extends Neo4jRepository<Donation, UUID> {
    @Query("MATCH (d:Donation)-[:DONATION_FROM]->(g:Group) " +
            "WHERE d.id = $donationId AND g.groupname = $groupname " +
            "RETURN CASE WHEN d IS NOT NULL THEN true ELSE false END")
    boolean hasDonationFromRelation(UUID donationId, String groupname);
}

