package com.fatec.donation.domain.images;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@Node("UserImages")
@Data
public class UserImages {
    @Id
    private UUID id;
    @Property("name")
    private String name;
    @Property("imageLink")
    private String imageLink;

    public UserImages() {
        this.id = UUID.randomUUID();
    }
}
