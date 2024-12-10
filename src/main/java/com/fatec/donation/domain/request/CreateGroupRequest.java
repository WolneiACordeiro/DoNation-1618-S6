package com.fatec.donation.domain.request;
import com.fatec.donation.domain.images.GroupImages;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CreateGroupRequest {
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
    private GroupImages groupImage;
    private GroupImages landscapeImage;
}
