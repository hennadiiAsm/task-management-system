package ru.effectivemobile.tms.persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findAllByAuthor_Id(Long authorId, Pageable pageable);

    List<TaskEntity> findAllByExecutor_Id(Long executorId, Pageable pageable);

    List<TaskEntity> findAllByAuthor_IdAndExecutor_Id(Long authorId, Long executorId, Pageable pageable);

}
