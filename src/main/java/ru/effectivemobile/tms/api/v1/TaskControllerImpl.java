package ru.effectivemobile.tms.api.v1;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.effectivemobile.tms.api.v1.exception.NotAllowedException;
import ru.effectivemobile.tms.dto.ErrorResponse;
import ru.effectivemobile.tms.dto.task.TaskCreationDto;
import ru.effectivemobile.tms.dto.task.TaskOutgoingDto;
import ru.effectivemobile.tms.dto.task.TaskPatchDto;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.TaskRepository;
import ru.effectivemobile.tms.persistence.repository.UserRepository;
import ru.effectivemobile.tms.service.TaskMapper;
import ru.effectivemobile.tms.service.TaskPatcher;
import ru.effectivemobile.tms.service.UserEntityExtractor;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskControllerImpl implements TaskController {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final UserRepository userRepository;

    private final UserEntityExtractor userEntityExtractor;

    private final TaskPatcher taskPatcher;

    public static final String DELETION_ERROR_MSG = "Current principal can't delete this task";

    @Override
    public ResponseEntity<TaskOutgoingDto> showById(Long id) {
        Optional<TaskEntity> optional = taskRepository.findById(id);
        return optional.map(entity -> ResponseEntity.ok(taskMapper.toOutgoingDto(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<TaskOutgoingDto>> showAll(String sort, int size, int page, Long authorId, Long executorId) {
        PageRequest pageRequest = pageRequest(page, size, sort);

        List<TaskEntity> result;
        if (authorId != null && executorId != null) {
            result = taskRepository.findAllByAuthor_IdAndExecutor_Id(authorId, executorId, pageRequest);
        } else if (authorId != null) {
            result = taskRepository.findAllByAuthor_Id(authorId, pageRequest);
        } else if (executorId != null) {
            result = taskRepository.findAllByExecutor_Id(executorId, pageRequest);
        } else {
            result = taskRepository.findAll(pageRequest).toList();
        }
        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.ok(result.stream().map(taskMapper::toOutgoingDto).collect(Collectors.toList()));
    }

    private PageRequest pageRequest(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];

        if (sortParams.length == 1) {
            return PageRequest.of(page, size, Sort.by(property));
        }

        Sort.Direction direction =  Sort.Direction.fromString(sortParams[1]);
        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    @Override
    public ResponseEntity<?> create(TaskCreationDto task, UriComponentsBuilder ucb, Authentication authentication) {
        UserEntity author = userEntityExtractor.extract(authentication);
        Optional<UserEntity> optional = userRepository.findById(task.executorId());
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponse.withPayload("No user with id=" + task.executorId()));
        }

        TaskEntity saved = taskRepository.save(taskMapper.toEntity(task, author, optional.get()));

        URI locationOfNewTask = ucb
                .path("api/v1/tasks/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTask).build();
    }

    @Override
    public ResponseEntity<Void> patchById(Long id, TaskPatchDto patchDto, Authentication authentication) {

        Optional<TaskEntity> optional = taskRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TaskEntity entity = optional.get();
        UserEntity principal = userEntityExtractor.extract(authentication);
        taskPatcher.patch(entity, patchDto, principal);
        taskRepository.save(entity);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteById(Long id, Authentication authentication) {

        Optional<TaskEntity> optional = taskRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TaskEntity entity = optional.get();
        UserEntity principal = userEntityExtractor.extract(authentication);
        if (!entity.getAuthor().equals(principal)) {
            throw new NotAllowedException(DELETION_ERROR_MSG);
        }

        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
