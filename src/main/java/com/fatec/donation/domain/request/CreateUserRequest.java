package com.fatec.donation.domain.request;

import com.fatec.donation.domain.enums.Roles;
import com.fatec.donation.domain.images.UserImages;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CreateUserRequest {
    @Null
    private UserImages userImage;
    @Size(min = 3, max = 50, message = "Seu nome deve ter entre 3 a 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]*$", message = "Seu nome deve conter apenas letras")
    private String name;
    @Size(min = 3, max = 16, message = "Nome de usuário deve ter entre 3 a 16 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Nome de usuário deve conter apenas letras, números e underscores")
    private String username;
    @Email(message = "Precisa ser um email válido")
    private String email;
    @NotBlank(message = "Campo obrigatório")
    @Size(min = 8, message = "Senha deve ter 8 caracteres ou mais")
    private String password;
    private LocalDateTime createdAt;
}
