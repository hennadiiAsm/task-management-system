package ru.effectivemobile.tms.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import ru.effectivemobile.tms.persistence.entity.RefreshTokenEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByOwner(UserEntity owner);

}
