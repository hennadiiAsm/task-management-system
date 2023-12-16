package ru.effectivemobile.tms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.effectivemobile.tms.persistence.entity.UserEntity;

public record AuthRequestDto(@Email(regexp = UserEntity.EMAIL_PATTERN) @NotBlank String email, @NotBlank String password) {

}
