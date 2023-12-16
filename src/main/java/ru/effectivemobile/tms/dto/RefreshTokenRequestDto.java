package ru.effectivemobile.tms.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(@NotBlank String token) {

}