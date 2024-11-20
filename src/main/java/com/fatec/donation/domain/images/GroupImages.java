package com.fatec.donation.domain.images;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@Node("GroupImages")
@Getter
@Setter
public class GroupImages {
    @Id
    private UUID id;
    @Property("name")
    private String name;
    @Property("imageLink")
    private String imageLink;

    public GroupImages() {
        this.id = UUID.randomUUID();
    }
}
