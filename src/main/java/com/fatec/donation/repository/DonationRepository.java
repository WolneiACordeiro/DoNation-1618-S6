package com.fatec.donation.repository;

import com.fatec.donation.domain.dto.DonationDTO;
import com.fatec.donation.domain.dto.DonationSearchDTO;
import com.fatec.donation.domain.entity.Donation;
import com.fatec.donation.domain.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.UUID;

public interface DonationRepository extends Neo4jRepository<Donation, UUID> {
    @Query("MATCH (d:Donation)-[:DONATION_FROM]->(g:Group) " +
            "WHERE d.id = $donationId AND g.groupname = $groupname " +
            "RETURN CASE WHEN d IS NOT NULL THEN true ELSE false END")
    boolean hasDonationFromRelation(UUID donationId, String groupname);

    @Query("MATCH (g:Group {id: $groupId})<-[:DONATION_FROM]-(d:Donation) " +
            "WITH g, d, $searchTerm AS searchTerm " +
            "WHERE searchTerm = '' OR d.name CONTAINS searchTerm " +
            "RETURN d " +
            "ORDER BY d.createdAt DESC")
    List<DonationSearchDTO> findDonationsByGroupAndSearchTerm(UUID groupId, String searchTerm);
}

