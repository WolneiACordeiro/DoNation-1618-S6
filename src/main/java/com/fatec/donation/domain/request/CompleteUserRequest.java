package com.fatec.donation.domain.request;

import com.fatec.donation.domain.enums.BrazilStates;
import com.fatec.donation.utils.CompatibleWithEnum;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class CompleteUserRequest {
    @NotBlank(message = "O número de telefone é obrigatório")
    @Pattern(regexp = "^\\(\\d{2}\\)\\d{5}-\\d{4}$", message = "O número de telefone é inválido, considere: (XX)XXXXX-XXXX")
    private String phone;
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate birthday;
    @NotNull(message = "O estado é obrigatório")
    private BrazilStates state;
    @NotBlank(message = "A cidade é obrigatória")
    private String city;
    @NotNull(message = "A lista de tags não pode ser nula")
    @Size(min = 1, message = "Pelo menos uma tag é obrigatória")
    private List<@NotBlank(message = "A tag não pode estar em branco") String> tags;
}
