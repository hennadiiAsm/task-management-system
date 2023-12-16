package ru.effectivemobile.tms.dto.task;

import ru.effectivemobile.tms.dto.comment.CommentOutgoingDto;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;

import java.util.List;

public record TaskOutgoingDto(Long id,
                              String title,
                              String description,
                              TaskEntity.Status status,
                              int priority,
                              Long authorId,
                              Long executorId,
                              List<CommentOutgoingDto> comments) {

}
