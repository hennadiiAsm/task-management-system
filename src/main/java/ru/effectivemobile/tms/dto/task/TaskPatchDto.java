package ru.effectivemobile.tms.dto.task;

import ru.effectivemobile.tms.persistence.entity.TaskEntity;

public record TaskPatchDto(String title,
                           String description,
                           TaskEntity.Status status,
                           Integer priority,
                           Long executorId) {

}
