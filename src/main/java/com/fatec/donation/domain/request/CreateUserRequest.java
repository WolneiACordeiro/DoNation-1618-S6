package com.fatec.donation.domain.request;

import com.fatec.donation.domain.enums.Roles;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CreateUserRequest {
    @Size(min = 3, max = 50, message = "Seu nome deve ter entre 3 a 50 caracteres")
    private String name;
    @Size(min = 3, max = 16, message = "Nome de usuário deve ter entre 3 a 16 caracteres")
    private String username;
    @Email(message = "Precisa ser um email válido")
    private String email;
    @NotBlank(message = "Campo obrigatório")
    @Size(min = 8, message = "Senha deve ter 8 caracteres ou mais")
    private String password;
    private LocalDateTime createdAt;
    @NotEmpty(message = "Campo obrigatório")
    private Set<Roles> roles;
}
