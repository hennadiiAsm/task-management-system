package ru.effectivemobile.tms.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.effectivemobile.tms.persistence.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

}
