package com.fatec.donation.repository;

import com.fatec.donation.domain.entity.ChatMessage;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import java.util.UUID;

public interface ChatMessageRepository extends Neo4jRepository<ChatMessage, UUID> {
}

