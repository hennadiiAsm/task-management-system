package ru.effectivemobile.tms.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.tms.dto.task.TaskCreationDto;
import ru.effectivemobile.tms.dto.task.TaskOutgoingDto;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskMapper {

    private final CommentMapper commentMapper;

    public TaskOutgoingDto toOutgoingDto(TaskEntity entity) {
        return new TaskOutgoingDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getAuthor().getId(),
                entity.getExecutor().getId(),
                entity.getComments().stream().map(commentMapper::toOutgoingDto).collect(Collectors.toList())
        );
    }

    public TaskEntity toEntity(TaskCreationDto dto, UserEntity author, UserEntity executor) {
        return new TaskEntity(
                null,
                dto.title(),
                dto.description(),
                TaskEntity.Status.PENDING,
                dto.priority(),
                author,
                executor,
                Collections.emptyList()
        );
    }

}
