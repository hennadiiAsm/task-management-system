package ru.effectivemobile.tms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.tms.api.v1.exception.IllegalFieldValueException;
import ru.effectivemobile.tms.api.v1.exception.NotAllowedException;
import ru.effectivemobile.tms.dto.task.TaskPatchDto;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskPatcher {

    private final UserRepository userRepository;

    public void patch(TaskEntity entity, TaskPatchDto dto, UserEntity principal) {
        if (entity.getAuthor().equals(principal)) {
            patchAsAuthor(entity, dto);
        } else if (entity.getExecutor().equals(principal)) {
            patchAsExecutor(entity, dto);
        } else {
            throw new NotAllowedException("Current principal can't patch this task");
        }
    }

    private void patchAsAuthor(TaskEntity entity, TaskPatchDto dto) {
        if (dto.status() != null) throw new NotAllowedException("Only executor can change task status");

        if (dto.title() != null) {
            if (dto.title().length() < 2 || dto.title().length() > 50) {
                throw new IllegalFieldValueException("min=2, max=50", "title", dto.title());
            }
            entity.setTitle(dto.title());
        }
        if (dto.description() != null) {
            if (dto.description().length() < 2 || dto.description().length() > 500) {
                throw new IllegalFieldValueException("min=2, max=500", "description", dto.description());
            }
            entity.setDescription(dto.description());
        }
        if (dto.priority() != null) {
            if (dto.priority() < 1 || dto.priority() > 10) {
                throw new IllegalFieldValueException("min=1, max=10", "priority", dto.priority());
            }
            entity.setPriority(dto.priority());
        }
        if (dto.executorId() != null) {
            Optional<UserEntity> optional = userRepository.findById(dto.executorId());
            if (optional.isEmpty()) {
                throw new IllegalFieldValueException("Executor not found", "executor", dto.executorId());
            }
            entity.setExecutor(optional.get());
        }
    }

    private void patchAsExecutor(TaskEntity entity, TaskPatchDto dto) {
        if (dto.title() != null) throw new NotAllowedException("Only author can change task title");
        if (dto.description() != null) throw new NotAllowedException("Only author can change task description");
        if (dto.priority() != null) throw new NotAllowedException("Only author can change task priority");
        if (dto.executorId() != null) throw new NotAllowedException("Only author can change task executor");

        if (dto.status() != null) entity.setStatus(dto.status());
    }

}
