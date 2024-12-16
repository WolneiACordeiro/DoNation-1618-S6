package com.fatec.donation.domain.images;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.UUID;

@Node("DonationImages")
@Getter
@Setter
public class DonationImages {
    @Id
    private UUID id;
    @Property("name")
    private String name;
    @Property("imageLink")
    private String imageLink;

    public DonationImages() {
        this.id = UUID.randomUUID();
    }
}
