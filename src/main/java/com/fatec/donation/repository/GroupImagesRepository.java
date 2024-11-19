package com.fatec.donation.repository;

import com.fatec.donation.domain.images.GroupImages;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupImagesRepository extends Neo4jRepository<GroupImages, UUID> {
    @Query("MATCH (u:Group {id: $groupId})<-[:PROFILE_IMAGE]-(img:GroupImages) " +
            "RETURN img")
    Optional<GroupImages> findByGroupIdProfile(UUID groupId);

    @Query("MATCH (u:Group {id: $groupId})<-[:LANDSCAPE_IMAGE]-(img:GroupImages) " +
            "RETURN img")
    Optional<GroupImages> findByGroupIdLandscape(UUID groupId);

    @Query("MATCH (g:Group) " +
            "OPTIONAL MATCH (g)<-[r:PROFILE_IMAGE|LANDSCAPE_IMAGE]-(i:GroupImages) " +
            "WHERE g.id = $groupId AND type(r) = $type " +
            "RETURN i LIMIT 1")
    GroupImages findByGroupIdAndType(@Param("groupId") UUID groupId, @Param("type") String type);

}

