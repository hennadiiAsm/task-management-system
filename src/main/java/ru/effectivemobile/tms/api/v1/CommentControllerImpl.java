package ru.effectivemobile.tms.api.v1;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.effectivemobile.tms.api.v1.exception.NotAllowedException;
import ru.effectivemobile.tms.dto.ErrorResponse;
import ru.effectivemobile.tms.dto.comment.CommentCreationDto;
import ru.effectivemobile.tms.persistence.entity.CommentEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.CommentRepository;
import ru.effectivemobile.tms.persistence.repository.TaskRepository;
import ru.effectivemobile.tms.service.CommentMapper;
import ru.effectivemobile.tms.service.UserEntityExtractor;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentControllerImpl implements CommentController {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final TaskRepository taskRepository;

    private final UserEntityExtractor userEntityExtractor;

    @Override
    public ResponseEntity<?> create(Long taskId, CommentCreationDto comment, UriComponentsBuilder ucb, Authentication authentication) {

        var optional = taskRepository.findById(taskId);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponse.withPayload("No task with id=" + taskId));
        }
        UserEntity author = userEntityExtractor.extract(authentication);
        CommentEntity saved = commentRepository.save(commentMapper.toEntity(comment, author, optional.get()));

        URI locationOfNewTask = ucb
                .path("api/v1/tasks/{id}/comments/{id}")
                .buildAndExpand(taskId, saved.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewTask).build();
    }

    @Override
    public ResponseEntity<Void> deleteById(@PathVariable String taskId, Long commentId, Authentication authentication) {

        Optional<CommentEntity> optional = commentRepository.findById(commentId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CommentEntity comment = optional.get();
        UserEntity principal = userEntityExtractor.extract(authentication);
        if (!comment.getAuthor().equals(principal)) {
            throw new NotAllowedException("Current principal can't delete this comment");
        }

        commentRepository.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
