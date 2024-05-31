package com.fatec.donation.domain.entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCursedWord {
    private Boolean inappropriate;

    public boolean isInappropriate() {
        return Boolean.TRUE.equals(inappropriate);
    }

}
