package ru.effectivemobile.tms.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.effectivemobile.tms.persistence.entity.RefreshTokenEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration-time-minutes}")
    private long expirationTimeMinutes;

    public RefreshTokenEntity getRefreshToken(UserEntity owner) {

        Optional<RefreshTokenEntity> optional = refreshTokenRepository.findByOwner(owner);
        if (optional.isPresent()) {
            RefreshTokenEntity existingToken = optional.get();
            if (existingToken.getExpiryDate().isAfter(Instant.now())) {
                return existingToken;
            } else {
                refreshTokenRepository.delete(existingToken);
            }
        }

        RefreshTokenEntity newToken = RefreshTokenEntity.builder()
                .owner(owner)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(expirationTimeMinutes, ChronoUnit.MINUTES))
                .build();
        return refreshTokenRepository.save(newToken);
    }

    public Optional<RefreshTokenEntity> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public void verifyExpiration(RefreshTokenEntity token){
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BadCredentialsException("Refresh token is expired");
        }
    }

}