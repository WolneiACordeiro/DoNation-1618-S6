package com.fatec.donation.domain.request;

import com.fatec.donation.domain.images.GroupImages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.*;

@AllArgsConstructor
@Getter
public class UpdateGroupRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String description;
    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres")
    private String address;
    private GroupImages groupImage;
    private GroupImages landscapeImage;
}
