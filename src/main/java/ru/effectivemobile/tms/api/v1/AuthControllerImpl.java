package ru.effectivemobile.tms.api.v1;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.effectivemobile.tms.dto.AuthRequestDto;
import ru.effectivemobile.tms.dto.ErrorResponse;
import ru.effectivemobile.tms.dto.JwtResponseDto;
import ru.effectivemobile.tms.dto.RefreshTokenRequestDto;
import ru.effectivemobile.tms.persistence.entity.RefreshTokenEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.service.JwtService;
import ru.effectivemobile.tms.service.RefreshTokenService;
import ru.effectivemobile.tms.service.UserEntityExtractor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RestController
public class AuthControllerImpl implements AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final UserEntityExtractor userEntityExtractor;

    @Override
    public JwtResponseDto login(AuthRequestDto authRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.email(), authRequestDto.password()));

        if (authentication.isAuthenticated()) {
            UserEntity principal = userEntityExtractor.extract(authentication);
            return new JwtResponseDto(
                    jwtService.generateToken(authRequestDto.email()),
                    refreshTokenService.getRefreshToken(principal).getToken()
            );
        } else {
            throw new BadCredentialsException("Wrong credentials");
        }
    }

    @Override
    public JwtResponseDto refreshToken(RefreshTokenRequestDto dto) {

        Optional<RefreshTokenEntity> optional = refreshTokenService.findByToken(dto.token());
        if (optional.isEmpty()) {
            throw new BadCredentialsException("Wrong credentials");
        }

        RefreshTokenEntity refreshToken = optional.get();
        refreshTokenService.verifyExpiration(refreshToken);

        UserEntity owner = refreshToken.getOwner();
        return new JwtResponseDto(
                jwtService.generateToken(owner.getEmail()),
                refreshTokenService.getRefreshToken(owner).getToken()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleCredentialsException(BadCredentialsException ex) {
        return ErrorResponse.withPayload(ex.getMessage());
    }
}
