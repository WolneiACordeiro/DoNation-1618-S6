package com.fatec.donation.repository;

import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends Neo4jRepository<User, UUID> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<UserDTO> findUserDTOById(UUID id);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);

    @Query("MATCH (g:User {username: $userName}) RETURN g.id")
    UUID findIdByUsername(@Param("userName") String userName);


}
