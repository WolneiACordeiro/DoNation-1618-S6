package com.fatec.donation.repository;

import com.fatec.donation.domain.request.DonationRequest;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface DonationRequestRepository extends Neo4jRepository<DonationRequest, UUID> {
}

