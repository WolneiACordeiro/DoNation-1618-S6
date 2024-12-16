package com.fatec.donation.domain.entity;

import com.fatec.donation.domain.enums.WeekDays;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Date {
    @Id
    private UUID id;

    @NotNull(message = "O dia da semana não pode ser nulo")
    private WeekDays day;

    @Size(min = 1, message = "Pelo menos um horário disponível é obrigatório")
    private List<@NotBlank(message = "O horário não pode estar em branco") String> avaliableTime;

    public Date() {
        this.id = UUID.randomUUID();
    }
}
