package ru.effectivemobile.tms.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreationDto(@NotBlank String content) {

}
