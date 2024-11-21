package com.fatec.donation.repository;

import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.dto.UserOwnerDTO;
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

    @Query("MATCH (g:Group)-[:OWNER]->(u:User) WHERE g.id = $groupId RETURN u")
    User findOwnerByGroupId(@Param("groupId") UUID groupId);

    @Query("MATCH (g:Group {id: $groupId})-[:OWNER]->(u:User) RETURN u.name AS name, u.username AS username, u.email AS email;")
    UserOwnerDTO findOwnerDTOByGroupId(@Param("groupId") UUID groupId);

    Optional<User> findUserByUsername(String username);

    String getUsernameById(UUID id);

    @Query("MATCH (g:User {username: $userName}) RETURN g.id")
    UUID findIdByUsername(@Param("userName") String userName);


}
