package com.fatec.donation.domain.dto;

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
@AllArgsConstructor
public class DateDTO {
    private WeekDays day;
    private List<String> avaliableTime;
}
