package com.fatec.donation.repository;

import com.fatec.donation.domain.dto.CompleteUserDTO;
import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends Neo4jRepository<User, UUID> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<UserDTO> findUserDTOById(UUID id);
    Optional<User> findUserByEmail(String email);
    CompleteUserDTO findCompleteUserDTOById(UUID io);

}
