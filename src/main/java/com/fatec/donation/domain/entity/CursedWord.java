package com.fatec.donation.domain.entity;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
public class CursedWord {
    @SerializedName("text")
    private String text;
}
