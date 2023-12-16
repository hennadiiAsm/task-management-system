package ru.effectivemobile.tms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.effectivemobile.tms.api.v1.TaskControllerImpl;
import ru.effectivemobile.tms.dto.ErrorResponse;
import ru.effectivemobile.tms.dto.task.TaskCreationDto;
import ru.effectivemobile.tms.dto.task.TaskOutgoingDto;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.TaskRepository;
import ru.effectivemobile.tms.persistence.repository.UserRepository;
import ru.effectivemobile.tms.service.JwtService;
import ru.effectivemobile.tms.service.TaskMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TaskControllerTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String URL_PATH = "/api/v1/tasks";
    private static UserEntity principal;
    private static String JWT_TOKEN;

    @BeforeAll
    static void init(@Autowired UserRepository userRepository,
                     @Autowired PasswordEncoder passwordEncoder,
                     @Autowired JwtService jwtService) {

        principal = userRepository.save(new UserEntity(null, "a@a", passwordEncoder.encode("password"), null));
        JWT_TOKEN = jwtService.generateToken(principal.getEmail());
    }

    @AfterEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    void whenGetAndUnauthorized() throws Exception {
        mvc.perform(get(URL_PATH))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenGetAndIncorrectToken() throws Exception {
        mvc.perform(get(URL_PATH)
                        .header("Authorization", "Bearer TRASH_VALUE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenGetWithCorrectTokenAndNoTasks() throws Exception {
        mvc.perform(get(URL_PATH)
                        .header("Authorization", "Bearer " + JWT_TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenGetWithCorrectTokenAndTasksExist() throws Exception {
        TaskEntity saved1 = taskRepository.save(new TaskEntity(
                null, "Sample", "Do everything on time!", TaskEntity.Status.PENDING, 6, principal, principal, List.of()));
        TaskEntity saved2 = taskRepository.save(new TaskEntity(
                null, "Sample", "Do everything on time!", TaskEntity.Status.PROCESSING, 5, principal, principal, List.of()));

        String content = mvc.perform(get(URL_PATH)
                        .header("Authorization", "Bearer " + JWT_TOKEN))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        var list = (List< TaskOutgoingDto>) objectMapper.readValue(content, TypeFactory.defaultInstance().constructCollectionType(List.class, TaskOutgoingDto.class));
        assertThat(list).containsExactly(taskMapper.toOutgoingDto(saved1), taskMapper.toOutgoingDto(saved2));
    }

    @Test
    void whenGetAndIdNotExist() throws Exception {
        mvc.perform(get(URL_PATH + "/-1")
                        .header("Authorization", "Bearer " + JWT_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAndIdExist() throws Exception {
        TaskEntity saved = taskRepository.save(new TaskEntity(
                null, "Sample", "Do everything on time!", TaskEntity.Status.PENDING, 6, principal, principal, List.of()));

        mvc.perform(get(URL_PATH + "/" + saved.getId())
                        .header("Authorization", "Bearer " + JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(taskMapper.toOutgoingDto(saved))));
    }

    @Test
    void whenPostAndExecutorNotExist() throws Exception {

        var taskCreationDto = new TaskCreationDto("aa", "bb", 9, -1);

        mvc.perform(post(URL_PATH)
                        .header("Authorization", "Bearer " + JWT_TOKEN)
                        .content(objectMapper.writeValueAsString(taskCreationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"));

        assertThat(taskRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void whenPostValid() throws Exception {

        UserEntity executor = userRepository.save(new UserEntity(null, "b@b", passwordEncoder.encode("password"), null));
        var taskCreationDto = new TaskCreationDto("aa", "bb", 9, executor.getId());

        mvc.perform(post(URL_PATH)
                        .header("Authorization", "Bearer " + JWT_TOKEN)
                        .content(objectMapper.writeValueAsString(taskCreationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(taskRepository.findAll()).isNotEmpty();

        userRepository.delete(executor);
    }

    @Test
    void whenDeleteAndPrincipalIsNotAuthor() throws Exception {

        UserEntity author = userRepository.save(new UserEntity(
                null, "b@b", passwordEncoder.encode("password"), null));
        TaskEntity saved = taskRepository.save(new TaskEntity(
                null, "Sample", "Do everything on time!", TaskEntity.Status.PENDING, 6, author, author, List.of()));

        String expectedJsonContent = objectMapper.writeValueAsString(ErrorResponse.withPayload(TaskControllerImpl.DELETION_ERROR_MSG));

        mvc.perform(delete(URL_PATH + "/" + saved.getId())
                        .header("Authorization", "Bearer " + JWT_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(content().json(expectedJsonContent));

        assertTrue(taskRepository.findById(saved.getId()).isPresent());

        taskRepository.deleteAll();
        userRepository.delete(author);
    }

    @Test
    void whenDeleteAndPrincipalIsAuthor() throws Exception {
        TaskEntity saved = taskRepository.save(new TaskEntity(
                null, "Sample", "Do everything on time!", TaskEntity.Status.PENDING, 6, principal, principal, List.of()));

        mvc.perform(delete(URL_PATH + "/" + saved.getId())
                        .header("Authorization", "Bearer " + JWT_TOKEN))
                .andExpect(status().isNoContent());

        assertTrue(taskRepository.findById(saved.getId()).isEmpty());
    }

    @AfterAll
    static void finalCleanUp(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
    }
}
