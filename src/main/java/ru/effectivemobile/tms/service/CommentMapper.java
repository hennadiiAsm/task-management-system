package ru.effectivemobile.tms.service;

import org.springframework.stereotype.Service;
import ru.effectivemobile.tms.dto.comment.CommentCreationDto;
import ru.effectivemobile.tms.dto.comment.CommentOutgoingDto;
import ru.effectivemobile.tms.persistence.entity.CommentEntity;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;

@Service
public class CommentMapper {

    public CommentOutgoingDto toOutgoingDto(CommentEntity entity) {
        return new CommentOutgoingDto(entity.getId(), entity.getAuthor().getId(), entity.getContent());
    }

    public CommentEntity toEntity(CommentCreationDto dto, UserEntity author, TaskEntity task) {
        return new CommentEntity(null, author, task, dto.content());
    }

}
