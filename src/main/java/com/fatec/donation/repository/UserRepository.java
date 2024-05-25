package com.fatec.donation.repository;

import com.fatec.donation.domain.dto.UserDTO;
import com.fatec.donation.domain.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    UserDTO findUserById(Long id);
    User findUserByEmail(String email);
    @Query("MATCH (user:User), (course:Course) WHERE user.username = $username AND course.identifier = $identifier " +
            "RETURN EXISTS((user)-[:ENROLLED_IN]->(course))")
    Boolean findEnrolmentStatus(String username, String identifier);

//    @Query("MATCH (user:User), (course:Course) WHERE user.username = $username AND course.identifier = $identifier " +
//            "CREATE (user)-[:ENROLLED_IN]->(course) RETURN user, course")
//    CourseEnrolmentQueryResult createEnrolmentRelationship(String username, String identifier);
}
