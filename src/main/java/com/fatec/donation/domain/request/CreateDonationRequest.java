package com.fatec.donation.domain.request;

import com.fatec.donation.domain.entity.Date;
import com.fatec.donation.domain.images.DonationImages;
import com.fatec.donation.domain.images.GroupImages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
public class CreateDonationRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String description;
    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String address;
    @Size(min = 1, message = "Pelo menos uma tag é obrigatória")
    private List<@NotBlank(message = "A tag não pode estar em branco") String> tags;
    private LocalDateTime createdAt;
    @Size(min = 1, message = "Pelo menos uma data é obrigatória")
    @Property("date")
    private HashSet<@Valid Date> avaliableDate;
    private DonationImages donationImage;
    private String availability;
}
