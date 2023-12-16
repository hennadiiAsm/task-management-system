package ru.effectivemobile.tms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.effectivemobile.tms.dto.AuthRequestDto;
import ru.effectivemobile.tms.dto.JwtResponseDto;
import ru.effectivemobile.tms.dto.RefreshTokenRequestDto;
import ru.effectivemobile.tms.persistence.entity.RefreshTokenEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.UserRepository;
import ru.effectivemobile.tms.service.RefreshTokenService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthControllerTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private static UserEntity user;
    private static final String VALID_EMAIL = "a@a";
    private static final String VALID_PASSWORD = "password";

    @BeforeAll
    static void init(@Autowired UserRepository userRepository, @Autowired PasswordEncoder passwordEncoder) {
        user = userRepository.save(new UserEntity(null, VALID_EMAIL, passwordEncoder.encode(VALID_PASSWORD), null));
    }

    @Test
    void correctEmailPassword() throws Exception {

        var authRequestDto = new AuthRequestDto(VALID_EMAIL, VALID_PASSWORD);

        byte[] content = mvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsByteArray();

        validateResponseContent(content);
    }

    private void validateResponseContent(byte[] content) throws IOException {
        JwtResponseDto jwtResponseDto = objectMapper.readValue(content, JwtResponseDto.class);

        assertThat(jwtResponseDto.accessToken().split("\\.").length).isEqualTo(3);
        assertThat(jwtResponseDto.refreshToken()).isNotBlank();
    }

    @Test
    void correctRefreshToken() throws Exception {
        RefreshTokenEntity refreshToken = refreshTokenService.getRefreshToken(user);
        var refreshTokenRequestDto = new RefreshTokenRequestDto(refreshToken.getToken());

        byte[] content = mvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsByteArray();

        validateResponseContent(content);
    }

    @Test
    void wrongRefreshToken() throws Exception {
        RefreshTokenEntity refreshToken = refreshTokenService.getRefreshToken(user);
        var refreshTokenRequestDto = new RefreshTokenRequestDto(refreshToken.getToken() + "TRASH");

        mvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongEmail() throws Exception {
        final String wrongEmail = "b@b";
        var authRequestDto = new AuthRequestDto(wrongEmail, VALID_PASSWORD);
        performUnauthorizedRequest(authRequestDto);
    }

    void performUnauthorizedRequest(AuthRequestDto authRequestDto) throws Exception {
        mvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPassword() throws Exception {
        final String wrongPassword = "WRONG";
        var authRequestDto = new AuthRequestDto(VALID_EMAIL, wrongPassword);
        performUnauthorizedRequest(authRequestDto);
    }

    @Test
    void invalidPassword() throws Exception {
        final String invalidPassword = " ";
        var authRequestDto = new AuthRequestDto(VALID_EMAIL, invalidPassword);
        performBadRequest(authRequestDto);
    }

    void performBadRequest(AuthRequestDto authRequestDto) throws Exception {
        mvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidEmail() throws Exception {
        final String invalidEmail = "a@";
        var authRequestDto = new AuthRequestDto(invalidEmail, VALID_PASSWORD);
        performBadRequest(authRequestDto);
    }

    @AfterAll
    static void cleanUp(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
    }
}
