package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.Date;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.HashSet;
import java.util.UUID;

public interface DateRepository extends Neo4jRepository<Date, UUID> {

    @Query("MATCH (d:Donation {id: $donationId})-[:AVALIABLE_DATE]->(date:Date) " +
            "RETURN date.avaliableTime AS avaliableTime, date.day AS day")
    HashSet<Date> findAvailableDatesByDonationId(UUID donationId);

}

