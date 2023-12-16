package ru.effectivemobile.tms.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.effectivemobile.tms.persistence.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

}
