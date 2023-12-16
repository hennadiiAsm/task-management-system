package ru.effectivemobile.tms;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effectivemobile.tms.persistence.entity.CommentEntity;
import ru.effectivemobile.tms.persistence.entity.TaskEntity;
import ru.effectivemobile.tms.persistence.entity.UserEntity;
import ru.effectivemobile.tms.persistence.repository.CommentRepository;
import ru.effectivemobile.tms.persistence.repository.TaskRepository;
import ru.effectivemobile.tms.persistence.repository.UserRepository;

import java.util.List;

@SpringBootApplication
public class TaskManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementSystemApplication.class, args);
    }

//    @Bean
//    ApplicationRunner applicationRunner(UserRepository userRepository,
//                                        TaskRepository taskRepository,
//                                        PasswordEncoder passwordEncoder,
//                                        CommentRepository commentRepository) {
//        return args -> {
//            UserEntity user = new UserEntity(null, "email@mail.com", passwordEncoder.encode("password"), null);
//            userRepository.save(user);
//
//            TaskEntity task =
//                    new TaskEntity(null, "Sample","Do everything on time!", TaskEntity.Status.PROCESSING, 5, user, user, List.of());
//            taskRepository.save(task);
//
//            CommentEntity comment = new CommentEntity(null, user, task, "Nice task=(");
//            commentRepository.save(comment);
//        };
//    }

}
